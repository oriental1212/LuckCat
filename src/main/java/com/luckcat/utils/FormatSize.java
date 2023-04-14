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
    public String formatSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        return df.format((double) fileS / 1048576);
    }
    public String addStrings(String num1, String num2) {
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int carry = 0;
        StringBuilder sb = new StringBuilder();
        while (i >= 0 || j >= 0 || carry != 0) {
            int x = i >= 0 ? num1.charAt(i) - '0' : 0;
            int y = j >= 0 ? num2.charAt(j) - '0' : 0;
            int sum = x + y + carry;
            sb.append(sum % 10);
            carry = sum / 10;
            i--;
            j--;
        }
        return sb.reverse().toString();
    }
}
