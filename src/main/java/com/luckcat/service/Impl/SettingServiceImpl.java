package com.luckcat.service.Impl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dao.SettingMapper;
import com.luckcat.dto.SettingRevise;
import com.luckcat.pojo.Setting;
import com.luckcat.service.SettingService;
import com.luckcat.utils.LuckResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class SettingServiceImpl extends ServiceImpl<SettingMapper, Setting> implements SettingService {
    @Resource
    private SettingMapper settingMapper;
    //总体用户修改设置
    @Override
    @Transactional
    public LuckResult ReviseUserSetting(SettingRevise settingRevise) {
        //查询是否为admin用户
        if(!StpUtil.hasRole("admin")){
            LuckResult.error("权限不合法");
        }
        //查询是否存在总体用户设置,不存在就存一个，存在就修改
        Setting flag;
        try {
            QueryWrapper<Setting> settingQueryWrapper = new QueryWrapper<>();
            settingQueryWrapper.eq("user_id",1);
            flag = settingMapper.selectOne(settingQueryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LuckCatError("总体用户设置查询失败");
        }
        //存在
        if(flag != null){
            try {
                Setting newSetting = new Setting();
                newSetting.setUserId(1L);
                newSetting.setStorageSpace(settingRevise.getStorageSpace());
                newSetting.setStorageSize(settingRevise.getStorageSize());
                newSetting.setStorageQuantity(settingRevise.getStorageQuantity());
                settingMapper.insert(newSetting);
            } catch (Exception e) {
                e.printStackTrace();
                throw new LuckCatError("插入总体设置失败");
            }
            return LuckResult.success("插入总体设置成功");
        //不存在
        }else {
            try {
                UpdateWrapper<Setting> settingUpdateWrapper = new UpdateWrapper<>();
                settingUpdateWrapper.eq("user_id",1);
                settingUpdateWrapper.set("storage_space",settingRevise.getStorageSpace());
                settingUpdateWrapper.set("storage_size",settingRevise.getStorageSize());
                settingUpdateWrapper.set("storage_quantity",settingRevise.getStorageQuantity());
                settingMapper.update(null,settingUpdateWrapper);
            } catch (Exception e) {
                e.printStackTrace();
                throw new LuckCatError("更新总体设置失败");
            }
            return LuckResult.success("更新总体设置成功");
        }
    }

    @Override
    public LuckResult ReviseUserSettingOne(SettingRevise settingRevise) {
        //查询是否为admin用户
        if(!StpUtil.hasRole("admin")){
            LuckResult.error("权限不合法");
        }
        //查询是否存在单个用户设置,不存在就存一个，存在就修改
        Setting flag;
        try {
            QueryWrapper<Setting> settingQueryWrapper = new QueryWrapper<>();
            settingQueryWrapper.eq("user_id",StpUtil.getLoginIdAsLong());
            flag = settingMapper.selectOne(settingQueryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LuckCatError("总体用户设置查询失败");
        }
        //存在
        if(flag != null){
            try {
                Setting newSetting = new Setting();
                newSetting.setUserId(StpUtil.getLoginIdAsLong());
                newSetting.setStorageSpace(settingRevise.getStorageSpace());
                newSetting.setStorageSize(settingRevise.getStorageSize());
                newSetting.setStorageQuantity(settingRevise.getStorageQuantity());
                settingMapper.insert(newSetting);
            } catch (Exception e) {
                e.printStackTrace();
                throw new LuckCatError("插入单个用户设置失败");
            }
            return LuckResult.success("插入单个用户设置失败");
            //不存在
        }else {
            try {
                UpdateWrapper<Setting> settingUpdateWrapper = new UpdateWrapper<>();
                settingUpdateWrapper.eq("user_id",StpUtil.getLoginIdAsLong());
                settingUpdateWrapper.set("storage_space",settingRevise.getStorageSpace());
                settingUpdateWrapper.set("storage_size",settingRevise.getStorageSize());
                settingUpdateWrapper.set("storage_quantity",settingRevise.getStorageQuantity());
                settingMapper.update(null,settingUpdateWrapper);
            } catch (Exception e) {
                e.printStackTrace();
                throw new LuckCatError("更新单个用户设置失败");
            }
            return LuckResult.success("更新单个用户设置失败");
        }
    }
}
