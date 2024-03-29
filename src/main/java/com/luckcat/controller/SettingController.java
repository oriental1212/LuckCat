package com.luckcat.controller;

import com.luckcat.dto.SettingRevise;
import com.luckcat.service.SettingService;
import com.luckcat.utils.LuckResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (Setting)表控制层
 *
 * @author Oriental
 * @since 2023-04-11 12:24:56
 */
@RestController
@RequestMapping("setting")
@Api("用户设置操作接口")
@Slf4j
public class SettingController {
    /**
     * 服务对象
     */
    @Resource
    private SettingService settingService;

    /**
     * 用户设置修改
     *
     * @param settingRevise 用户设置
     * @return success
     */
    @ApiOperation("总体用户修改设置")
    @PostMapping("/reviseUserSetting")
    public LuckResult ReviseUserSetting(@RequestBody SettingRevise settingRevise){
        if(settingRevise.getStorageSpace() == null || settingRevise.getStorageSize() == null){
            return LuckResult.error("参数为空");
        }
        return settingService.ReviseUserSetting(settingRevise);
    }

    /**
     * 获取用户设置数据
     */
    @ApiOperation("获取用户设置数据")
    @PostMapping("getSetting")
    public LuckResult getSetting(){
        return settingService.getSetting();
    }
}
