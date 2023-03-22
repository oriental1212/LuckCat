package com.luckcat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luckcat.pojo.Photo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {
}
