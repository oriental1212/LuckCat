package com.luckcat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luckcat.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
