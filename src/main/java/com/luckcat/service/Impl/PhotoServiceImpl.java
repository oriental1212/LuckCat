package com.luckcat.service.Impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dao.PhotoMapper;
import com.luckcat.dao.UserMapper;
import com.luckcat.dto.PhotoAdd;
import com.luckcat.dto.PhotoPage;
import com.luckcat.pojo.Photo;
import com.luckcat.pojo.User;
import com.luckcat.service.PhotoService;
import com.luckcat.utils.LuckResult;
import com.luckcat.utils.MinioInit;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;


@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private MinioInit minioInit;
    @Resource
    private PhotoMapper photoMapper;

    //上传图片
    @Override
    @Transactional
    public LuckResult upload(MultipartFile file, PhotoAdd photoAdd) {
        Date date = DateUtil.date();
        String today= DateUtil.today();
        //获取图片名字
        String photoname = today + "-" + file.getOriginalFilename().substring(0,file.getOriginalFilename().indexOf("."));
        //根据username查询userid
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username",photoAdd.getUserName());
        Long userid;
        try {
            userid = userMapper.selectOne(userQueryWrapper).getUid();
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new LuckCatError("没有查询到用户id,用户未存在，或请重新查询");
        }
        //获取图片类型
        String begin = file.getContentType().substring(0, file.getContentType().indexOf("/"));
        String phototype = file.getContentType().substring(begin.length()+1);
        //获取标签
        String phototag = photoAdd.getPhotoTag();
        //上传图片并且获取url
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)){
            throw new LuckCatError("图片名字为空");
        }
        InputStream fileInputStream=null;
        //设置文件储存的文件夹路劲
        String objectName = DateUtil.year(date) + "/" + (DateUtil.month(date) + 1) + "/" + DateUtil.dayOfMonth(date) + "/" + photoname;
        try {
            fileInputStream = file.getInputStream();
            PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(minioInit.getBucketName()).object(objectName)
                    .stream(fileInputStream, file.getSize(), -1).contentType(file.getContentType()).build();
            //文件名称相同会覆盖
            minioInit.createMinio().putObject(objectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LuckCatError("图片上传失败");
        } finally {
            try {
                if (fileInputStream!=null){
                    fileInputStream.close();
                }
            }catch (Exception e){
                throw new LuckCatError("图片上传失败");
            }

        }
        //获取文件地址
        String photourl;
        GetPresignedObjectUrlArgs build = new GetPresignedObjectUrlArgs().builder().bucket(minioInit.getBucketName()).object(photoname).method(Method.GET).build();
        try {
            photourl = minioInit.createMinio().getPresignedObjectUrl(build);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LuckCatError("文件地址获取失败");
        }
        //图像数据插入数据库
        Photo newphoto = new Photo();
        newphoto.setPhotoName(photoname);
        newphoto.setUserId(userid);
        newphoto.setPhotoType(phototype);
        newphoto.setPhotoTag(phototag);
        newphoto.setPhotoUrl(photourl);
        newphoto.setPhotoCreatTime(date);
        photoMapper.insert(newphoto);
        return LuckResult.success("文件上传成功");
    }

    //获取单个用户的图片
    @Override
    public LuckResult queryByUsername(PhotoPage photoPage) {
        //查询用户id
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("photo_name",photoPage.getUsername());
        Long userid;
        try {
            userid = userMapper.selectOne(userQueryWrapper).getUid();
        } catch (NullPointerException e) {
            e.printStackTrace();
            throw new LuckCatError("没有查询到用户id,用户未存在，或请重新查询");
        }
        //查询图片
        try{
            QueryWrapper<Photo> photoQueryWrapper = new QueryWrapper<>();
            photoQueryWrapper.select("photo_name","photo_tag","photo_url","photo_creat_time")
                    .eq("user_id",userid);
            Page<Photo> Page = new Page<>(photoPage.getPage(),photoPage.getSize());
            photoMapper.selectPage(Page,photoQueryWrapper);
            List<Photo> records = Page.getRecords();
            return LuckResult.success(records);
        }catch (LuckCatError error){
            error.printStackTrace();
            throw new LuckCatError("未查询成功，请稍后重试");
        }
    }

    //下载图片
    @Override
    public void download(String filename, HttpServletResponse response){
        String[] split = filename.split("-");
        String filePath=Integer.parseInt(split[0])+"/"+Integer.parseInt(split[1])+"/"+Integer.parseInt(split[2])+"/"+filename;
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(minioInit.getBucketName()).object(filePath).build();
        try (GetObjectResponse res = minioInit.createMinio().getObject(objectArgs)){
            byte[] buf = new byte[1024];
            int len;
            try (FastByteArrayOutputStream os = new FastByteArrayOutputStream()){
                while ((len=res.read(buf))!=-1){
                    os.write(buf,0,len);
                }
                os.flush();
                byte[] bytes = os.toByteArray();
                response.setCharacterEncoding("utf-8");
                // 设置强制下载不打开
                // res.setContentType("application/force-download");
                response.addHeader("Content-Disposition", "attachment;fileName=" + filename);
                try (ServletOutputStream stream = response.getOutputStream()){
                    stream.write(bytes);
                    stream.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LuckCatError("下载失败");
        }
    }
}
