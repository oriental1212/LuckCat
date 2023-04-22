package com.luckcat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luckcat.dto.OssRevise;
import com.luckcat.pojo.Oss;
import com.luckcat.utils.LuckResult;
import org.springframework.web.bind.annotation.PathVariable;

public interface OssService extends IService<Oss> {
    public LuckResult GetAllOssInfo();
    public LuckResult ChangeOssInfo(OssRevise ossRevise);
    public LuckResult ChangeOssState(String ossName, String ossState);
}
