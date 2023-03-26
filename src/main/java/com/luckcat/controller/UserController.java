package com.luckcat.controller;



import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dto.UserLogin;
import com.luckcat.dto.UserRegister;
import com.luckcat.pojo.User;
import com.luckcat.service.UserService;
import com.luckcat.utils.LuckResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import static com.luckcat.utils.LuckResult.success;

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
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param user 查询实体
     * @return 所有数据
     */
    @GetMapping
    public LuckResult selectAll(Page<User> page, User user) {
        return success(this.userService.page(page, new QueryWrapper<>(user)));
    }

    /**
     * 登录用户接口
     *
     * @param userLogin 实体对象
     * @return 登录结果
     */
    @ApiOperation("登录用户接口")
    @GetMapping("/loginUser")
    public LuckResult login(@RequestBody UserLogin userLogin) {
        if(userLogin.getAccount() != null && userLogin.getPassword() != null){
            userService.LoginUser(userLogin);
            return success("登录成功的喔！");
        }
        return success("账号和密码不能为空的喔！");
    }

    /**
     * 注册用户接口
     *
     * @param userRegister 实体对象
     * @return 新增结果
     */
    @ApiOperation("新增用户接口")
    @PostMapping("/registerUser")
    public LuckResult register(@RequestBody UserRegister userRegister) {
        //邮箱的正则校验
        String EmailMatch = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(userRegister.getEmail() != null && userRegister.getEmail().matches(EmailMatch)){
            if(userRegister.getPassword() != null && userRegister.getUsername() != null){
                SaResult saResult = userService.addUser(userRegister);
                return success(saResult);
            }
        }
        return LuckResult.error("参数不合法，请重新传递");
    }

    /**
     * 查询用户在线接口
     *
     * @return 登录状态结果
     */
    @ApiOperation("查询用户在线接口")
    @GetMapping("/isLoginUser")
    public LuckResult isLogin() {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        if(tokenInfo.getIsLogin()){
            Map<String, Object> returnMap = new HashMap<>();
            try {
                Long uid = (Long) tokenInfo.getLoginId();
                StpUtil.logout(uid);
                StpUtil.login(uid);
                returnMap.put("msg","查询到用户已登录，token已更新");
                returnMap.put("newToken",StpUtil.getTokenInfo());
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
     * @param email,url 邮箱和找回密码的地址
     * @return 成功找到，且发送邮件
     */
    @ApiOperation("发送找回密码邮件接口")
    @GetMapping("/findPasswordMail/{email}/{url}")
    public LuckResult findPasswordMail(@PathVariable("email") String email,@PathVariable("url") String url) {
        String EmailMatch = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(email.matches(EmailMatch) && !url.isEmpty()){
            return userService.findPasswordMail(email,url);
        }
        return LuckResult.error("邮箱格式不对哟");
    }

    /**
     * 找回密码接口
     *
     * @param email,url 邮箱和找回密码的地址
     * @return 成功找到，且发送邮件
     */
    @ApiOperation("找回密码接口")
    @GetMapping("/updatePassword/{email}/{password}")
    public LuckResult updatePassword(@PathVariable("email") String email,@PathVariable("password") String password){
        String EmailMatch = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if(email.matches(EmailMatch) && !password.isEmpty()){
            return userService.updatePassword(email,password);
        }
        return LuckResult.error("邮箱格式不对哟");
    }

}

