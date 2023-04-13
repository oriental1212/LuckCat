package com.luckcat.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luckcat.pojo.Photo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {

    @Select("select photo_type as name ,count(photo_type) as value from photo group by photo_type")
    @ResultType(Map.class)
    List<Map<Object,Object>> countByPhotoType();
}
