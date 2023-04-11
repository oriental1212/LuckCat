package com.luckcat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Oriental
 * @version 1.0
 * @description 用户设置修改
 * @date 2023/4/11 14:29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingRevise {
    //用户空间
    private String storageSpace;
    //单个图片大小
    private String storageSize;
    //图片数量
    private String storageQuantity;
}
