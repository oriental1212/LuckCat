package com.luckcat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luckcat.dto.OssRevise;
import com.luckcat.pojo.Oss;
import com.luckcat.utils.LuckResult;

public interface OssService extends IService<Oss> {
    public LuckResult GetAllOssInfo();
    public LuckResult ChangeOssInfo(OssRevise ossRevise);
}
