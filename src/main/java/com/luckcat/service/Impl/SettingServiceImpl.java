package com.luckcat.service.Impl;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dao.SettingMapper;
import com.luckcat.dto.SettingRevise;
import com.luckcat.pojo.Setting;
import com.luckcat.service.SettingService;
import com.luckcat.utils.LuckResult;
import com.luckcat.vo.SettingView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
        //查询修改数值是否已经小于使用数值了
        //查询是否存在总体用户设置,不存在就存一个，存在就修改
        Setting flag;
        //最大用户的使用量
        String storageUsedMax;
        try {
            QueryWrapper<Setting> settingQueryWrapper = new QueryWrapper<>();
            settingQueryWrapper.select("MAX("+ "storage_used" +")");
            List<Object> objects = settingMapper.selectObjs(settingQueryWrapper);
            settingQueryWrapper.eq("user_id",1).select("storage_space");
            flag = settingMapper.selectOne(settingQueryWrapper);
            storageUsedMax = (String) objects.get(0);

            //查询修改数值是否已经小于使用数值了
            if(Integer.parseInt(settingRevise.getStorageSpace()) > Double.parseDouble(storageUsedMax)){
                //不存在
                if(flag == null){
                    try {
                        Setting newSetting = new Setting();
                        newSetting.setUserId(1L);
                        newSetting.setStorageSpace(settingRevise.getStorageSpace());
                        newSetting.setStorageSize(settingRevise.getStorageSize());
                        newSetting.setStorageUsed("0");
                        settingMapper.insert(newSetting);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new LuckCatError("500","插入总体设置失败");
                    }
                    return LuckResult.success("插入总体设置成功");
                    //存在
                }else {
                    try {
                        UpdateWrapper<Setting> settingUpdateWrapper = new UpdateWrapper<>();
                        settingUpdateWrapper.eq("user_id",1);
                        settingUpdateWrapper.set("storage_space",settingRevise.getStorageSpace());
                        settingUpdateWrapper.set("storage_size",settingRevise.getStorageSize());
                        settingMapper.update(null,settingUpdateWrapper);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new LuckCatError("500","更新总体设置失败");
                    }
                    return LuckResult.success("更新总体设置成功");
                }
            }else{
                LuckResult.error("修改的数值小于已使用值");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LuckCatError("500","总体用户设置查询失败");
        }
        return null;
    }
    //获取设置数据
    @Override
    public LuckResult getSetting() {
        //查询是否为admin用户
        if(!StpUtil.hasRole("admin")){
            LuckResult.error("权限不合法");
        }
        //获取存储量和图片单个大小
        SettingView settingView = new SettingView();
        Setting setting = settingMapper.selectOne(new LambdaQueryWrapper<Setting>().eq(Setting::getUserId, 1));
        settingView.setStorageSpace(setting.getStorageSpace());
        settingView.setStorageSize(setting.getStorageSize());
        //获取最大使用量
        QueryWrapper<Setting> settingQueryWrapper = new QueryWrapper<>();
        settingQueryWrapper.select("MAX("+ "storage_used" +")");
        List<Object> objects = settingMapper.selectObjs(settingQueryWrapper);
        settingView.setStorageMaxUsed((String) objects.get(0));
        return LuckResult.success(settingView);
    }
}
