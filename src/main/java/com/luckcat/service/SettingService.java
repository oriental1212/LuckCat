package com.luckcat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luckcat.dto.SettingRevise;
import com.luckcat.pojo.Setting;
import com.luckcat.utils.LuckResult;

public interface SettingService extends IService<Setting> {
    LuckResult ReviseUserSetting(SettingRevise settingRevise);
    LuckResult getSetting();
}
