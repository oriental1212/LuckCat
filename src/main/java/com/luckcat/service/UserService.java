package com.luckcat.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luckcat.dto.UserLogin;
import com.luckcat.dto.UserRegister;
import com.luckcat.pojo.User;
import com.luckcat.utils.LuckResult;

public interface UserService extends IService<User> {
    SaResult addUser(UserRegister userRegister);
    SaResult LoginUser(UserLogin userLogin);
    LuckResult findPasswordMail(String email, String url);
    LuckResult updatePassword(String email,String password);
    LuckResult findAllUser();
    LuckResult disableUser(String username);
}
