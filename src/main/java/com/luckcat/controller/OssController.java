package com.luckcat.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dto.OssRevise;
import com.luckcat.service.OssService;
import com.luckcat.utils.LuckResult;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Oriental
 * @version 1.0
 * @description TODO
 * @date 2023/4/20 14:55
 */
@RestController
@RequestMapping("oss")
@Api("Oss对象存储接口")
public class OssController {
    @Resource
    private OssService ossService;

    /**
     * 获取所有oss服务器信息接口
     *
     * @param
     * @return 所有oss服务信息
     */
    @ApiOperation("获取所有oss服务器信息接口")
    @GetMapping("/getAllOssInfo")
    public LuckResult GetAllOssInfo(){
        if(StpUtil.hasRole("admin")){
            return ossService.GetAllOssInfo();
        }else{
            return LuckResult.error("权限错误");
        }
    }

    /**
     * 更改Oss的账号和密码
     *
     * @param
     * @return 所有oss服务信息
     */
    @ApiOperation("更改Oss的账号和密码")
    @PostMapping("/changeOssInfo")
    public LuckResult ChangeOssInfo(@RequestBody OssRevise ossRevise){
        if(StpUtil.hasRole("admin")) {
            if(ossRevise.getAccessKey() != null && ossRevise.getSecretKey() != null){
                return ossService.ChangeOssInfo(ossRevise);
            }else{
                return LuckResult.error("账号和密码不能为空");
            }
        }else{
            return LuckResult.error("权限错误");
        }
    }

    /**
     * 更改Oss的的状态
     *
     * @param ossName，ossState
     * @return 更改结果
     */
    @ApiOperation("更改Oss的的状态")
    @GetMapping("/changeOssInfo/{ossName}/{ossState}")
    public LuckResult ChangeOssState(@PathVariable("ossName") String ossName,@PathVariable("ossState") String ossState){
        if(ossName != null && ossState != null){
            if(StpUtil.hasRole("admin")){
                return ossService.ChangeOssState(ossName,ossState);
            }
            else{
                LuckResult.error("用户权限不正确");
            }
        }
        return LuckResult.error("传递参数有误");
    }
}
