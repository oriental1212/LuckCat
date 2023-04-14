package com.luckcat.service.Impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FastByteArrayOutputStream;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luckcat.config.Exception.LuckCatError;
import com.luckcat.dao.PhotoMapper;
import com.luckcat.dao.SettingMapper;
import com.luckcat.dao.UserMapper;
import com.luckcat.dto.PhotoAdd;
import com.luckcat.dto.PhotoFont;
import com.luckcat.dto.PhotoPage;
import com.luckcat.pojo.Photo;
import com.luckcat.pojo.Setting;
import com.luckcat.pojo.User;
import com.luckcat.service.PhotoService;
import com.luckcat.utils.FormatSize;
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
    @Resource
    private SettingMapper settingMapper;

    //查询图片是否还有充足空间
    public boolean SettingUsedFind(MultipartFile file){
        QueryWrapper<Setting> settingQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Setting> settingQueryWrapper1 = new QueryWrapper<>();
        settingQueryWrapper.eq("user_id",StpUtil.getLoginIdAsLong());
        settingQueryWrapper1.eq("user_id",1);
        Setting setting = settingMapper.selectOne(settingQueryWrapper);
        FormatSize formatSize = new FormatSize();
        if(setting != null){
            return Integer.parseInt(setting.getStorageSpace()) > Integer.parseInt((formatSize.formatSize(file.getSize()) + setting.getStorageUsed()));
        }else{
            Setting setting1 = settingMapper.selectOne(settingQueryWrapper1);
            return Integer.parseInt(setting1.getStorageSpace()) > Integer.parseInt((formatSize.formatSize(file.getSize()) + setting1.getStorageUsed()));
        }
    }
    //上传图片
    @Override
    @Transactional
    public synchronized LuckResult upload(MultipartFile file, PhotoAdd photoAdd) {
        if(!SettingUsedFind(file)){
            LuckResult.error("存储空间不足");
        }
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
            PutObjectArgs objectArgs = PutObjectArgs.builder()
                    .bucket(minioInit.getBuckNameOfPhoto())
                    .object(objectName)
                    .stream(fileInputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
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
                throw new LuckCatError("文件流关闭失败");
            }

        }
        //获取文件地址
        String photourl;
        GetPresignedObjectUrlArgs build = GetPresignedObjectUrlArgs
                .builder()
                .bucket(minioInit.getBuckNameOfPhoto())
                .object(objectName)
                .method(Method.GET)
                .build();
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
        try {
            photoMapper.insert(newphoto);
        } catch (Exception e) {
            throw new LuckCatError("新增图像数据失败");
        }
        //减少存储空间
        FormatSize formatSize = new FormatSize();
        QueryWrapper<Setting> settingQueryWrapper = new QueryWrapper<>();
        QueryWrapper<Setting> settingQueryWrapper1 = new QueryWrapper<>();
        UpdateWrapper<Setting> settingUpdateWrapper = new UpdateWrapper<>();
        settingQueryWrapper.eq("user_id", StpUtil.getLoginIdAsLong());
        settingQueryWrapper1.eq("user_id", 1);
        try {
            Setting settingByUser1 = settingMapper.selectOne(settingQueryWrapper);
            if(settingByUser1 != null){
                settingUpdateWrapper.eq("user_id",StpUtil.getLoginIdAsLong()).set("storage_used",formatSize.addStrings(settingByUser1.getStorageSpace(),formatSize.formatSize(file.getSize())));
                settingMapper.update(null,settingUpdateWrapper);
            }else{
                Setting settingByUser2 = settingMapper.selectOne(settingQueryWrapper1);
                settingUpdateWrapper.eq("user_id",1).set("storage_used",formatSize.addStrings(settingByUser2.getStorageSpace(),formatSize.formatSize(file.getSize())));
                settingMapper.update(null,settingUpdateWrapper);
            }
        } catch (Exception e) {
            throw new LuckCatError("空间修改出错");
        }
        return LuckResult.success("文件上传成功");
    }

    //获取单个用户的所有图片
    @Override
    public LuckResult queryByUsername(PhotoPage photoPage) {
        //查询用户id
        Long userid = StpUtil.getLoginIdAsLong();
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
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(minioInit.getBuckNameOfPhoto()).object(filePath).build();
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

    //查询所有的图片（admin）
    @Override
    public LuckResult findAllPhoto() {
        if(StpUtil.hasRole("admin")){
            return LuckResult.error("用户权限不合法");
        }
        QueryWrapper<Photo> photoQueryWrapper = new QueryWrapper<>();
        Long count = photoMapper.selectCount(photoQueryWrapper);
        if(!String.valueOf(count).equals("")){
            return LuckResult.success(count);
        }
        return LuckResult.error("查询错误");
    }

    //收藏图片
    @Override
    @Transactional
    public LuckResult PhotoLove(PhotoFont photoFont) {
        long userId = StpUtil.getLoginIdAsLong();
        try {
            UpdateWrapper<Photo> photoUpdateWrapper = new UpdateWrapper<>();
            photoUpdateWrapper.eq("user_id",userId).set("photo_tag","love");
            photoMapper.update(null,photoUpdateWrapper);
        } catch (Exception e) {
            throw new LuckCatError("图片收藏失败，请重试");
        }
        return LuckResult.success("收藏成功的喔！~");
    }

    //修改图片标签
    @Override
    @Transactional
    public LuckResult modifyLabel(PhotoFont photoFont) {
        //条件构造器
        LambdaQueryWrapper<Photo> photoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        photoLambdaQueryWrapper
                .eq(Photo::getPhotoName, photoFont.getPhotoName())
                .eq(Photo::getUserId, StpUtil.getLoginIdAsLong());
        try {
            //判断信息是否有效
            boolean exists = photoMapper.exists(photoLambdaQueryWrapper);
            if (!exists) {
                return LuckResult.error("修改失败！信息不正确");
            }
            LambdaUpdateWrapper<Photo> PhotoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            PhotoLambdaUpdateWrapper.eq(Photo::getUserId, StpUtil.getLoginIdAsLong()).eq(Photo::getPhotoName, photoFont.getPhotoName()).set(Photo::getPhotoName,photoFont.getPhotoTag());
            int update = photoMapper.update(null, PhotoLambdaUpdateWrapper);
            return update>0?LuckResult.success("修改成功"):LuckResult.error("修改失败,请稍后再试！");
        }catch (Exception e){
            throw new LuckCatError("修改失败,请稍后再试！");
        }
    }
}
