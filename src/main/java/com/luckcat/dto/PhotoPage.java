package com.luckcat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Oriental
 * @version 1.0
 * @description 用户分页查询类
 * @date 2023/4/1 12:35
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoPage {
    private String username;
    //页面大小
    private int size;
    //第几页
    private int page;

}
