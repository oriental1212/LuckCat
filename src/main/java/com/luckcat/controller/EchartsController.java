package com.luckcat.controller;

import com.luckcat.dao.PhotoMapper;
import com.luckcat.dto.Echarts;
import com.luckcat.service.PhotoService;
import com.luckcat.service.UserService;
import com.luckcat.utils.LuckResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/echarts")
@Api("控制台总览数据")
@Slf4j
public class EchartsController {

    @Resource
    private UserService userService;
    @Resource
    private PhotoService photoService;
    @Resource
    private PhotoMapper photoMapper;

    @GetMapping("")
    @ApiOperation("控制台总览数据")
    private LuckResult echartsData(){
        Echarts echartsData = new Echarts();
        //获取总人数
        long totalUser = userService.count();
        echartsData.setTotalUser(totalUser);
        //获取图片总数
        long totalPhoto = photoService.count();
        echartsData.setTotalPhoto(totalPhoto);
        /*
        获取各类图片(jpg,jpeg...)数量
            格式:type-count ;
            eg: jpg-1 ,jpeg-2
        * */
        List<Map<Object, Object>> barData = photoMapper.countByPhotoType();//[{photo_type=jpeg, count=3}, {photo_type=jpg, count=1}, {photo_type=png, count=1}]
        ArrayList<Object> barTitle = new ArrayList<>();
        barData.forEach(i-> {
            barTitle.add(i.get("name"));
        });
        echartsData.setBarTitle(barTitle);
        echartsData.setBarData(barData);
        //返回总结果
        return LuckResult.success(echartsData);
    }
}
