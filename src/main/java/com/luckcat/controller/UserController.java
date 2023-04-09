package com.luckcat.controller;



import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dto.UserLogin;
import com.luckcat.dto.UserRegister;
import com.luckcat.dto.UserRevise;
import com.luckcat.pojo.User;
import com.luckcat.service.UserService;
import com.luckcat.utils.LuckResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.luckcat.utils.LuckResult.*;

/**
 * (User)表控制层
 *
 * @author Oriental
 * @since 2023-03-23 09:06:07
 */
@RestController
@RequestMapping("user")
@Api("用户接口")
public class UserController  {
    @Resource
    private UserService userService;

    /**
     * 登出用户接口
     *
     */
    @ApiOperation("登出用户接口")
    @GetMapping("/logout")
    public LuckResult logout(){
        try {
            StpUtil.logout();
        } catch (LuckCatError e) {
            e.printStackTrace();
            return LuckResult.error("登出失败，重新尝试");
        }
        return LuckResult.success("登出成功");
    }

    /**
     * 禁用用户接口
     *
     * @param username 实体对象
     * @return 登录结果
     */
    @ApiOperation("禁用用户接口")
    @GetMapping("/disableUser")
    public LuckResult disableUser(@RequestParam("username") String username){
        return userService.disableUser(username);
    }

    /**
     * 查询所有用户
     *
     * @return 所有数据
     */
    @GetMapping("/findAllUser")
    @ApiOperation("查询所有用户接口")
    public LuckResult findAllUser(@RequestParam("currentPage") Integer currentPage,
                                  @RequestParam("pageSize") Integer pageSize
                                  ) {
        return userService.findAllUser(currentPage, pageSize);
    }

    /**
     * 登录用户接口
     *
     * @param userLogin 实体对象
     * @return 登录结果
     */
    @ApiOperation("登录用户接口")
    @PostMapping("/loginUser")
    public SaResult login(@RequestBody UserLogin userLogin) {
        if(userLogin.getAccount() != null && userLogin.getPassword() != null){
            return userService.LoginUser(userLogin);
        }
        return SaResult.error("账号和密码不能为空的喔！");
    }

    /**
     * 注册用户接口
     *
     * @param userRegister 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增用户接口")
    @PostMapping("/registerUser")
    public SaResult register(@RequestBody UserRegister userRegister) {
        //邮箱的正则校验
        String EmailMatch = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(userRegister.getEmail() != null && userRegister.getEmail().matches(EmailMatch)){
            if(userRegister.getPassword() != null && userRegister.getUsername() != null){
                return userService.register(userRegister);
            }
        }
        return SaResult.error("参数不合法，请重新传递");
    }

    /**
     * 用户续费token
     *
     * @return 登录状态结果
     */
    @ApiOperation("查询用户在线接口")
    @GetMapping("/renewalToken")
    public LuckResult RenewalToken() {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        if(tokenInfo.getIsLogin()){
            Map<String, Object> returnMap = new HashMap<>();
            try {
                Long uid = Long.parseLong((String) tokenInfo.getLoginId());
                StpUtil.logout(uid);
                StpUtil.login(uid);
                returnMap.put("info","查询到用户已登录，token已更新");
                returnMap.put("tokenValue",StpUtil.getTokenInfo());
            } catch (LuckCatError e) {
                throw new LuckCatError("更新token失败，重新请求更新");
            }
            return success(returnMap);
        }else{
            return LuckResult.error("未查询到用户，用户未登录或已过期,需要重新登录");
        }
    }

    /**
     * 发送找回密码邮件接口
     *
     * @param email 邮箱和找回密码的地址
     * @return 成功找到，且发送邮件
     */
    @ApiOperation("发送找回密码邮件接口")
    @GetMapping("/sendPasswordMail/{email}")
    public LuckResult SendPasswordMail(@PathVariable("email") String email) {
        String EmailMatch = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(email.matches(EmailMatch)){
            return userService.SendPasswordMail(email);
        }
        return LuckResult.error("邮箱格式不对哟");
    }

    /**
     * 验证码校验接口
     *
     * @param email,captcha 邮箱和验证码
     */
    @ApiOperation("验证码校验接口")
    @GetMapping("/captchaCheck/{email}/{captcha}")
    public LuckResult CaptchaCheck(@PathVariable("email") String email,@PathVariable("captcha") String captcha) {
        String EmailMatch = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(email.matches(EmailMatch)){
            return userService.CaptchaCheck(email,captcha);
        }
        return LuckResult.error("邮箱格式不对哟");
    }

    /**
     * 密码更改接口
     *
     * @param email,captcha,password 邮箱、校验码、密码
     */
    @ApiOperation("密码更改接口")
    @GetMapping("/updatePassword/{email}/{captcha}/{password}")
    public LuckResult updatePassword(@PathVariable("email") String email,@PathVariable("captcha") String captcha,@PathVariable("password") String password){
        String EmailMatch = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(email.matches(EmailMatch) && !password.isEmpty()){
            return userService.updatePassword(email,captcha,password);
        }
        return LuckResult.error("邮箱格式不对哟");
    }

    /**
     * 个人资料修改
     *
     * @param  userRevise 用户更改实体类
     */
    @ApiOperation("个人资料修改")
    @PostMapping("/personalRevise")
    public LuckResult PersonalRevise (@RequestBody UserRevise userRevise){
        if((userRevise.getPassword() != null) || (userRevise.getEmail() != null) || (userRevise.getNickname() != null)){
            return userService.PersonalRevise(userRevise);
        }
        return LuckResult.error("没有需要修改的数据哟");
    }

    /**
     * 头像修改
     *
     * @param file 用户更改实体类
     * @return 返回用户的头像地址
     */
    @ApiOperation("头像修改")
    @PostMapping("/avatarChange")
    public LuckResult AvatarChange (@RequestBody MultipartFile file){
        if(file.getSize() != 0){
            return userService.AvatarChange(file);
        }
        return LuckResult.error("您传入的图片为空，请重试");
    }

    /**
     * 判断用户是否存在
     * @param account
     * @return
     */
    @ApiOperation("判断用户名是否存在")
    @GetMapping("/exist/{account}")
    public LuckResult isExist(@PathVariable("account") String account){
        if (account != null && !account.equals("")) {
            return userService.isExist(account);
        }
        return LuckResult.error("信息错误,不能为空");
    }

    /**
     * 判断用户是否存在
     * @return userinfo
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/getPersonalInfo")
    public LuckResult GetPersonalInfo(){
        long uid = StpUtil.getLoginIdAsLong();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("uid",uid)
                .select("username","email","nickname","avatar_address");
        User personal =  userService.getOne(userQueryWrapper);
        if(personal != null){
            return LuckResult.success(personal);
        }else {
            return LuckResult.error("查询失败");
        }
    }

}

