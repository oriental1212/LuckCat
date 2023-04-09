package com.luckcat.config.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luckcat.dao.UserMapper;
import com.luckcat.pojo.User;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oriental
 * @version 1.0
 * @description 获取权限合集
 * @date 2023/4/3 14:30
 */

@Component
public class StpInterfaceImpl implements StpInterface {
    @Resource
    UserMapper userMapper;

    //判断用户的权限
    private String getAuthority(Object userId){
        long userIdLong = Long.parseLong(String.valueOf(userId));
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("authority").eq("uid",userIdLong);
        User user = userMapper.selectOne(userQueryWrapper);
        return user.getAuthority();
    }

    //为用户赋予权限
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    //为用户赋予角色
    @Override
    public List<String> getRoleList(Object o, String s) {
        List<String> list = new ArrayList<>();
        if(getAuthority(o).equals("admin")){
            list.add("admin");
            return list;
        }
        if(getAuthority(o).equals("user")){
            list.add("user");
            return list;
        }
        if(getAuthority(o).equals("disable")){
            list.add("disable");
            return list;
        }
        return null;
    }
}
