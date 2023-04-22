package com.luckcat.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 控制台总览数据
 */
@Data
public class Echarts {
    //用户总数
    private Long totalUser;
    //图片总数
    private Long totalPhoto;
    //饼图标题
    private List<Object> barTitle;
    //饼图数据
    private List<Map<Object,Object>> barData;
}
