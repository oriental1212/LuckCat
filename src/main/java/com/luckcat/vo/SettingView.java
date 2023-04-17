package com.luckcat.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Oriental
 * @version 1.0
 * @description TODO
 * @date 2023/4/17 14:29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingView {
    private String storageSpace;
    private String storageSize;
    private String storageMaxUsed;
}
