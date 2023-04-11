package com.luckcat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luckcat.pojo.Setting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SettingMapper extends BaseMapper<Setting> {
}
