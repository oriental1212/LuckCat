package com.luckcat.service.Impl;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dao.SettingMapper;
import com.luckcat.dao.UserMapper;
import com.luckcat.dto.UserLogin;
import com.luckcat.dto.UserRegister;
import com.luckcat.dto.UserRevise;
import com.luckcat.pojo.Setting;
import com.luckcat.pojo.User;
import com.luckcat.service.UserService;
import com.luckcat.utils.IdWorker;
import com.luckcat.utils.LuckResult;
import com.luckcat.utils.MinioInit;
import com.luckcat.utils.sendMail;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.luckcat.utils.CaptchaUtils.getCaptchaCode46;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    UserMapper userMapper;
    @Value("${server.port}")
    private String serverPort;

    @Resource
    sendMail sendMail;
    @Resource
    MinioInit minioInit;
    @Resource
    RedisTemplate<String, Integer> redisTemplate;
    @Resource
    SettingMapper settingMapper;

    //新增用户方法
    @Override
    @Transactional
    public synchronized SaResult register(UserRegister userRegister) {
        //校验该用户名或邮箱是否已经注册过
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getUsername,userRegister.getUsername())
                .or()
                .eq(User::getEmail,userRegister.getEmail());
        if (userMapper.exists(userLambdaQueryWrapper)) {
            return SaResult.error("此用户已经注册，不可以重复注册哟");
        }
        User newUser = new User();
        //为用户生成id
        long userid = new IdWorker(0, 0).nextId();
        //nickname默认为username
        //头像为默认头像
        String avatarAddress = "http://127.0.0.1:" + serverPort + "/default.jpg";
        //设置默认权限
        String authority = "user";
        //加密密码
        String newPassword = BCrypt.hashpw(userRegister.getPassword(), BCrypt.gensalt(10));
        //添加用户
        newUser.setUid(userid);
        newUser.setUsername(userRegister.getUsername());
        newUser.setPassword(newPassword);
        newUser.setEmail(userRegister.getEmail());
        newUser.setNickname(userRegister.getUsername());
        newUser.setAvatarAddress(avatarAddress);
        newUser.setAuthority(authority);
        userMapper.insert(newUser);
        //为用户开辟新的存储空间
        Setting setting = new Setting();
        setting.setUserId(userid);
        setting.setStorageUsed("0");
        settingMapper.insert(setting);
        //设置用户登录
        try {
            StpUtil.login(userid);
            ArrayList<Object> result = new ArrayList<>();
            //获取 Token  相关参数
            result.add(StpUtil.getTokenInfo());
            //获取用户信息
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("uid",userid)
                    .select("username","email","nickname","avatar_address");
            User user = userMapper.selectOne(userQueryWrapper);
            result.add(user);
            //返回前端
            return SaResult.data(result);
        } catch (SaTokenException e) {
            throw new LuckCatError("系统错误，生成id为空，请重新注册");
        }
    }

    //登录用户
    public SaResult LoginUser(UserLogin userLogin){
        //先通过用户名或者邮箱查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", userLogin.getAccount()).or().eq("email", userLogin.getAccount());
        User user = userMapper.selectOne(wrapper);
        //判断用户是否存在
        if (user == null) {
            return SaResult.error("该账号不存在！");
        }
        //判断密码是否正确
        if (BCrypt.checkpw(userLogin.getPassword(),user.getPassword())){
            try {
                StpUtil.login(user.getUid());
                List<Object> result = new ArrayList<>();
                //获取 Token  相关参数
                result.add(StpUtil.getTokenInfo());
                //获取用户信息
                wrapper.select("username","email","nickname","avatar_address");
                User newuser = userMapper.selectOne(wrapper);
                result.add(newuser);
                return SaResult.data(result);
            } catch (SaTokenException e) {
                throw new LuckCatError("系统错误，请重新登录");
            }
        }
        return SaResult.error("您的密码不正确哟！");
    }


    //找回密码邮件发送

    @Override
    public LuckResult SendPasswordMail(String email) {
        //校验该邮箱是否已经存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        if(userMapper.selectOne(wrapper) == null){
            return LuckResult.error("此用户不存在，请先注册哟");
        }
        try {
            Integer captcha = getCaptchaCode46(6);
            redisTemplate.opsForValue().set(email,captcha,5, TimeUnit.MINUTES);
            sendMail.sendTemplateMail(email, captcha);
            return LuckResult.success("验证码发送成功,五分钟内有效！");
        }catch (MailException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public LuckResult CaptchaCheck(String email, String captcha) {
        //校验该邮箱是否已经存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        if(userMapper.selectOne(wrapper) == null){
            return LuckResult.error("此用户不存在，请先注册哟");
        }
        //取出redis
        Integer captchaCheck = redisTemplate.opsForValue().get(email);
        if(captchaCheck == null){
            return LuckResult.error("验证码超时，请重新验证");
        }else if(captchaCheck == Integer.parseInt(captcha)){
            return LuckResult.success("验证码校验成功");
        }else {
            return LuckResult.error("验证码错误，请输入正确验证码");
        }
    }

    //找回密码功能
    @Transactional
    @Override
    public LuckResult updatePassword(String email,String captcha,String password){
        //校验该邮箱是否已经存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        if(userMapper.selectOne(wrapper) == null){
            return LuckResult.error("此用户不存在，请先注册哟");
        }
        //取出redis
        Integer captchaCheck = redisTemplate.opsForValue().get(email);
        if(captchaCheck == null){
            return LuckResult.error("验证码超时，请重新验证");
        }else if(captchaCheck == Integer.parseInt(captcha)){
            //更新密码
            try {
                String newPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
                User user = new User();
                user.setPassword(newPassword);
                UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("email", email);
                userMapper.update(user, updateWrapper);
            }catch (LuckCatError e){
                e.printStackTrace();
                throw new  LuckCatError("修改失败，请重试");
            }
            redisTemplate.delete(email);
            return LuckResult.success("修改成功");
        }else {
            return LuckResult.error("验证码错误，请输入正确验证码");
        }
    }

    //查询所有用户

    @Override
    public LuckResult findAllUser(Integer currentPage,Integer pageSize) {
        if(!StpUtil.hasRole("admin")){
            return LuckResult.error("用户权限不合法");
        }
        Page<User> userPage1;
        try {
            //分页构造器
            Page<User> userPage = new Page<>(currentPage, pageSize);
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("username","nickname","email","authority");
            userPage1 = userMapper.selectPage(userPage, userQueryWrapper);
        } catch (BeansException e) {
            throw new RuntimeException("未查询成功，请重新查询");
        }
        return LuckResult.success(userPage1);
    }

    //禁用用户
    public LuckResult disableUser(String username){
        if(!StpUtil.hasRole("admin")){
            return LuckResult.error("用户权限不合法");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("authority").eq("username",username);
        User user = userMapper.selectOne(userQueryWrapper);
        if(user.getAuthority().equals("admin")){
            return LuckResult.error("您无法对管理员用户禁用");
        }
        if(user.getAuthority().equals("disable")){
            return LuckResult.error("您无法对已禁用的用户继续禁用");
        }
        if(user.getAuthority().equals("user")){
            UpdateWrapper<User> userUpdateWrapper = new UpdateWrapper<>();
            userUpdateWrapper.eq("username",username).set("authority","disable");
            int update = userMapper.update(null, userUpdateWrapper);
            return update>0?LuckResult.success("禁用成功，禁用的用户名为：" + username):LuckResult.error("禁用失败");
        }
        return LuckResult.error("禁用失败，原因可能是未查到用户，或者无法禁用，请重试");
    }

    //修改用户个人资料
    @Override
    @Transactional
    public LuckResult PersonalRevise(UserRevise userRevise) {
        //校验用户是否被禁用
        if(StpUtil.hasRole("disable")){
            return LuckResult.error("用户权限不合法");
        }
        try {
            UpdateWrapper<User> userWrapper = new UpdateWrapper<>();
            long userId = StpUtil.getLoginIdAsLong();
            userWrapper.eq("uid",userId);
            if(userRevise.getEmail() != null){
                userWrapper.set("email",userRevise.getEmail());
            }
            if(userRevise.getNickname() != null){
                userWrapper.set("nickname",userRevise.getNickname());
            }
            userMapper.update(null,userWrapper);
        } catch (LuckCatError e) {
            e.printStackTrace();
            LuckResult.error("更新失败，请重试");
            throw e;
        }
        return LuckResult.success("你的资料更新成功");
    }

    //修改用户的图片
    @Override
    public LuckResult AvatarChange(MultipartFile file) {
        //校验用户是否被禁用
        if(StpUtil.hasRole("disable")){
            return LuckResult.error("用户权限不合法");
        }
        String userId = StpUtil.getLoginIdAsString();
        if(userId == null){
            LuckResult.error("用户未登录");
            throw new LuckCatError("用户未登录");
        }
        InputStream fileInputStream = null;
        //上传头像
        try {
            fileInputStream = file.getInputStream();
            PutObjectArgs build = PutObjectArgs.builder()
                    .bucket(minioInit.getBuckNameOfAvatar())
                    .object(userId)
                    .stream(fileInputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioInit.createMinio().putObject(build);
        } catch (IOException e) {
            LuckResult.error("文件流读取失败");
            throw new LuckCatError("文件流读取失败");
        } catch (ServerException | InsufficientDataException | ErrorResponseException | NoSuchAlgorithmException |
                 InvalidKeyException | InvalidResponseException | XmlParserException | InternalException e) {
            LuckResult.error("存入失败，重试");
            throw new LuckCatError("存入失败，重试");
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }catch (Exception e){
                LuckResult.error("文件流关闭失败");
                throw new LuckCatError("文件流关闭失败");
            }
        }
        //获取头像地址
        String photoUrl;
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs
                .builder()
                .bucket(minioInit.getBuckNameOfAvatar())
                .object(userId)
                .method(Method.GET)
                .build();
        try {
            photoUrl = minioInit.createMinio().getPresignedObjectUrl(build);
        } catch (ErrorResponseException | InsufficientDataException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | XmlParserException | ServerException | InternalException e) {
            LuckResult.error("文件Url获取失败");
            throw new LuckCatError("文件Url获取失败");
        }
        //修改数据库
        try {
            User user = new User();
            user.setAvatarAddress(photoUrl);
            UpdateWrapper<User> userWrapper = new UpdateWrapper<>();
            userWrapper.eq("uid",StpUtil.getLoginIdAsLong());
            userMapper.update(user,userWrapper);
        } catch (Exception e) {
            LuckResult.error("修改失败请重试");
            throw new LuckCatError("修改失败请重试");
        }
        return LuckResult.success(photoUrl);
    }
    //判断用户是否存在
    @Override
    public LuckResult isExist(String account) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper
                .eq(User::getUsername,account)
                .or()
                .eq(User::getEmail,account);
        boolean exists = userMapper.exists(userLambdaQueryWrapper);
        return exists?LuckResult.success("欢迎回来"):LuckResult.error("该用户不存在");
    }

}
