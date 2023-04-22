package com.luckcat.service;

import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.luckcat.dto.UserLogin;
import com.luckcat.dto.UserRegister;
import com.luckcat.dto.UserRevise;
import com.luckcat.pojo.User;
import com.luckcat.utils.LuckResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends IService<User> {
    SaResult register(UserRegister userRegister);
    SaResult LoginUser(UserLogin userLogin);
    LuckResult SendPasswordMail(String email);
    LuckResult CaptchaCheck(String email, String captcha);
    LuckResult updatePassword(String email,String captcha,String password);
    LuckResult findAllUser(Integer currentPage,Integer pageSize);
    LuckResult disableUser(String username);
    LuckResult AdminUser(String username);
    LuckResult CancelDisableUser(String username);
    LuckResult PersonalRevise(UserRevise userRevise);
    LuckResult AvatarChange(MultipartFile file);
    LuckResult isExist(String account);
    LuckResult GetUserAuthority();
}
