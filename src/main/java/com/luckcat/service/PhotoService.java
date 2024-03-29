package com.luckcat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.luckcat.dto.PhotoAdd;
import com.luckcat.dto.PhotoFont;
import com.luckcat.dto.PhotoPage;
import com.luckcat.pojo.Photo;
import com.luckcat.utils.LuckResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface PhotoService extends IService<Photo> {
    LuckResult upload(MultipartFile file, PhotoAdd photoAdd);
    LuckResult queryByUsername(PhotoPage photoPage);
    void download(String filename, HttpServletResponse response);
    LuckResult findAllPhoto();
    LuckResult PhotoLove(PhotoFont photoFont);
    LuckResult modifyLabel(PhotoFont photoFont);
    LuckResult deletePhoto(PhotoFont photoFont);
}
