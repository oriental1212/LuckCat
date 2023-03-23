package com.luckcat;

import com.luckcat.pojo.User;
import com.luckcat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class UserTest {
    @Autowired
    private UserService userService;
    //获取用户信息
    @Test
    public void getAll(){
        List<User> list = userService.list();
        log.info(list.toString());
    }
}
