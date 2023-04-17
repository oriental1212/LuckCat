package com.luckcat.utils;

import java.text.DecimalFormat;

/**
 * @author Oriental
 * @version 1.0
 * @description 根据文件的字节码转换成mb
 * @return 返回的是MB,参数为字节码长度
 * @date 2023/4/14 13:52
 */

public class FormatSize {
    public static String formatSize(long fileS) {
        DecimalFormat df = new DecimalFormat("0.00");
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        return df.format((double) fileS / 1048576);
    }
}
