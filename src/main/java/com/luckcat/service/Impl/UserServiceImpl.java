package com.luckcat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.dao.UserMapper;
import com.luckcat.pojo.User;
import com.luckcat.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
