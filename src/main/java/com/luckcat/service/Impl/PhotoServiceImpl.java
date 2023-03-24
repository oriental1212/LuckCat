package com.luckcat.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.dao.PhotoMapper;
import com.luckcat.pojo.Photo;
import com.luckcat.service.PhotoService;
import org.springframework.stereotype.Service;


@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {
}
