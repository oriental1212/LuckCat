package com.luckcat.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oriental
 * @version 1.0
 * @description 图片工具类
 * @date 2023/3/31 15:41
 */

@Component
public class PhotoUtils {
    @Bean
    public List AllPhotoType (){
        ArrayList<String> list = new ArrayList<>();
        list.add("image/jpeg");
        list.add("image/png");
        list.add("image/gif");
        list.add("image/jpg");
        return list;
    }
}
