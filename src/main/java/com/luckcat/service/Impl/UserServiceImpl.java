package com.luckcat.service.Impl;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dao.UserMapper;
import com.luckcat.dto.UserLogin;
import com.luckcat.dto.UserRegister;
import com.luckcat.pojo.User;
import com.luckcat.service.UserService;
import com.luckcat.utils.IdWorker;
import com.luckcat.utils.LuckResult;
import com.luckcat.utils.sendMail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    UserMapper userMapper;
    @Value("${server.port}")
    private String serverPort;

    @Resource
    sendMail sendMail;

    //新增用户方法
    @Override
    @Transactional
    public synchronized SaResult addUser(UserRegister userRegister) {
        //校验该邮箱是否已经注册过
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", userRegister.getEmail());
        if(userMapper.selectOne(wrapper) != null){
            return SaResult.data("此用户已经注册，不可以重复注册哟");
        }
        User newUser = new User();
        //为用户生成id
        long userid = new IdWorker(0, 0).nextId();
        //nickname默认为username
        //头像为默认头像
        String avatarAddress = "127.0.0.1:" + serverPort + "/default.jpg";
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
        //设置用户登录
        //先登录上
        SaTokenInfo tokenInfo;
        try {
            StpUtil.login(userid);
            //获取 Token  相关参数
            tokenInfo = StpUtil.getTokenInfo();
            //返回前端
            return SaResult.data(tokenInfo);
        } catch (SaTokenException e) {
            throw new LuckCatError("系统错误，生成id为空，请重新注册");
        }
    }

    //登录用户
    public SaResult LoginUser(UserLogin userLogin){
        SaTokenInfo tokenInfo;
        //先通过用户名或者邮箱查询
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", userLogin.getAccount()).or().eq("email", userLogin.getAccount());
        List<User> users = userMapper.selectList(wrapper);
        //遍历判断
        for (User user : users) {
            if (BCrypt.checkpw(userLogin.getPassword(),user.getPassword())){
                try {
                    StpUtil.login(user.getUid());
                    //获取 Token  相关参数
                    tokenInfo = StpUtil.getTokenInfo();
                    return SaResult.data(tokenInfo);
                } catch (SaTokenException e) {
                    throw new LuckCatError("系统错误，请重新登录");
                }
            }
        }
        return SaResult.data("您的密码不正确哟！");
    }

    //找回密码邮件发送

    @Override
    public LuckResult findPasswordMail(String email, String url) {
        //校验该邮箱是否已经存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("email", email);
        if(userMapper.selectOne(wrapper) == null){
            return LuckResult.error("此用户不存在，请先注册哟");
        }
        try {
            sendMail.sendTemplateMail(email,url);
        }catch (MailException e) {
            e.printStackTrace();
            throw e;
        }
        return LuckResult.success("发送成功");
    }

    //找回密码功能
    @Transactional
    @Override
    public LuckResult updatePassword(String email,String password){
        try {
            String newPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
            User user = new User();
            user.setPassword(newPassword);
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("email", email);
            userMapper.update(user, updateWrapper);
        }catch (LuckCatError e){
            e.printStackTrace();
            throw new  LuckCatError("修改失败，请重新修改");
        }
        return LuckResult.success("修改成功");
    }
}
