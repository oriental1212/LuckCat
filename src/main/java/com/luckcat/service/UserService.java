package com.luckcat.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luckcat.dto.UserLogin;
import com.luckcat.dto.UserRegister;
import com.luckcat.pojo.User;
import org.springframework.stereotype.Service;

public interface UserService extends IService<User> {
    SaResult addUser(UserRegister userRegister);
    SaResult LoginUser(UserLogin userLogin);
}
