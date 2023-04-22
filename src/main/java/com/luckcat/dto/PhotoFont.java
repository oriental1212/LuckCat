package com.luckcat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Oriental
 * @version 1.0
 * @description 前端图片类
 * @date 2023/4/14 12:35
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoFont {
    private String photoName;
    private String photoTag;
    private String photoUrl;
    private Date photoCreatTime;
}
