package com.luckcat.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dao.OssMapper;
import com.luckcat.dto.OssData;
import com.luckcat.dto.OssRevise;
import com.luckcat.pojo.Oss;
import com.luckcat.service.OssService;
import com.luckcat.utils.LuckResult;
import com.luckcat.utils.MinioInit;
import io.minio.messages.Bucket;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Oriental
 * @version 1.0
 * @description TODO
 * @date 2023/4/20 14:53
 */

@Service
public class OssServiceImpl extends ServiceImpl<OssMapper, Oss> implements OssService {
    @Resource
    OssMapper ossMapper;
    @Resource
    MinioInit minioInit;

    //获取所有oss服务器信息接口
    @Override
    public LuckResult GetAllOssInfo() {
        List<Oss> osses;
        try {
            LambdaQueryWrapper<Oss> wrapper = new LambdaQueryWrapper<>();
            osses = ossMapper.selectList(wrapper);
        } catch (Exception e) {
            throw new LuckCatError("对象存储信息查询失败");
        }
        OssData ossData = new OssData();
        List<OssData> results = new ArrayList<>();
        ArrayList<String> bucketNameList = new ArrayList<>();
        try {
            for (Oss oss : osses) {
                //遍历获取单个oss信息
                ossData.setOssName(oss.getName());
                ossData.setOssState(oss.getState());
                //之后要改工厂模式的
                List<Bucket> buckets = minioInit.createMinio().listBuckets();
                buckets.forEach((bucket) -> bucketNameList.add(bucket.name()));
                ossData.setOssBucketNames(bucketNameList);
                results.add(ossData);
            }
        } catch (Exception e){
            throw new LuckCatError("遍历赋值失败");
        }
        return LuckResult.success(results);
    }

    //更改Oss的账号和密码
    @Override
    public LuckResult ChangeOssInfo(OssRevise ossRevise) {
        if(InitOss(ossRevise)){
            LuckResult.success("未查询到该oss，已添加");
        }
        LambdaUpdateWrapper<Oss> updateWrapper = new LambdaUpdateWrapper<>();
        try {
            //不修改url
            if(ossRevise.getUrl().equals("")){
                updateWrapper
                        .eq(Oss::getName,ossRevise.getOssName())
                        .set(Oss::getAccessKey,ossRevise.getAccessKey())
                        .set(Oss::getSecretKey,ossRevise.getSecretKey());
                ossMapper.update(null,updateWrapper);
            }else{
                updateWrapper
                        .eq(Oss::getName,ossRevise.getOssName())
                        .set(Oss::getAccessKey,ossRevise.getAccessKey())
                        .set(Oss::getSecretKey,ossRevise.getSecretKey())
                        .set(Oss::getUrl,ossRevise.getUrl());
                ossMapper.update(null,updateWrapper);
            }
        } catch (Exception e) {
            throw new LuckCatError("更新修改失败");
        }
        return LuckResult.success("修改成功");
    }


    //更改Oss的的状态
    @Override
    public LuckResult ChangeOssState(String ossName, String ossState) {
        try {
            LambdaUpdateWrapper<Oss> wrapper = new LambdaUpdateWrapper<>();
            wrapper
                    .eq(Oss::getName,ossName)
                    .set(Oss::getState,ossState);
            ossMapper.update(null,wrapper);
        } catch (Exception e) {
            throw new LuckCatError("oss状态修改失败");
        }
        return LuckResult.success("oss状态修改成功");
    }

    //查询oss是否存在，如果不存在则新增
    private Boolean InitOss(OssRevise ossRevise){
        LambdaQueryWrapper<Oss> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Oss::getName,ossRevise.getOssName());
        if(ossMapper.selectOne(wrapper) != null){
            return false;
        }
        Oss oss = new Oss();
        oss.setName(ossRevise.getOssName());
        oss.setUrl(ossRevise.getUrl());
        oss.setAccessKey(ossRevise.getAccessKey());
        oss.setSecretKey(ossRevise.getSecretKey());
        ossMapper.insert(oss);
        return true;
    }
}
