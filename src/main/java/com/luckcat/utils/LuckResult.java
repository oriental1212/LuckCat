package com.luckcat.utils;

import lombok.Data;

/**
 * @author Oriental
 * @version 1.0
 * @description TODO
 * @date 2023/3/22 16:12
 */

@Data
public class LuckResult {
    private String code;
    private String msg;
    private Object data;

    private static  final String SUCCESS_CODE = "200";
    private static  final String SUCCESS_MSG = "请求成功";

    public static LuckResult success(){
        LuckResult result = new LuckResult();
        result.setCode(SUCCESS_CODE);
        result.setMsg(SUCCESS_MSG);
        return result;
    }

    public static LuckResult success(Object data){
        LuckResult result = success();
        result.setData(data);
        return result;
    }

    public static LuckResult error(String msg){
        LuckResult result = new LuckResult();
        result.setCode("500");
        result.setMsg(msg);
        return result;
    }

    public static LuckResult info(Object data){
        LuckResult result = new LuckResult();
        result.setCode(SUCCESS_CODE);
        result.setMsg("请求信息");
        result.setData(data);
        return result;
    }

}
