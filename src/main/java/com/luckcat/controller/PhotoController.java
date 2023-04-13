package com.luckcat.controller;

import com.luckcat.dto.PhotoAdd;
import com.luckcat.dto.PhotoPage;
import com.luckcat.pojo.Photo;
import com.luckcat.service.PhotoService;
import com.luckcat.utils.LuckResult;
import com.luckcat.utils.PhotoUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

import static com.luckcat.utils.LuckResult.*;

/**
 * (Photo)表控制层
 *
 * @author makejava
 * @since 2023-03-23 09:38:44
 */
@RestController
@RequestMapping("photo")
@Api("图像操作接口")
@Slf4j
public class PhotoController  {
    /**
     * 服务对象
     */
    @Resource
    private PhotoService photoService;

    @Resource
    private PhotoUtils photoUtils;


    /**
     * 文件下载
     *
     * @param fileName 分页查询类
     * @return List数据
     */
    @ApiOperation("文件下载")
    @GetMapping("/download/{fileName}")
    public LuckResult download(@PathVariable("fileName") String fileName,HttpServletResponse response) {
        if(!fileName.isEmpty()){
            photoService.download(fileName, response);
        }
        return success("下载成功");
    }

    /**
     * 通过用户查询所有图片地址
     *
     * @param photoPage 分页查询类
     * @return List数据
     */
    @ApiOperation("通过用户查询所有图片地址")
    @GetMapping("/queryByUsername")
    public LuckResult queryByUsername(@RequestBody PhotoPage photoPage) {
        if(!photoPage.getUsername().isEmpty() && photoPage.getPage()>0 && photoPage.getSize()>0){
            return photoService.queryByUsername(photoPage);
        }
        return error("你的用户名为空");
    }

    /**
     * 上传图像
     *
     * @param file,photoAdd 文件
     * @return 新增结果
     */
    @PostMapping("/upload")
    @ApiOperation("上传图片接口")
    public LuckResult upload(@RequestBody MultipartFile file,
                             @RequestParam("photoTag") String photoTag,
                             @RequestParam("userName") String userName) {
        for (Object type : photoUtils.AllPhotoType()) {
            if (Objects.equals(file.getContentType(), type)) {
                return photoService.upload(file,new PhotoAdd(userName,photoTag));
            }
        }
        return success("您的图片格式不对劲哟！");
    }

    /**
     *
     * 查询图库图片总数
     *
     * @return 总数结果
     */
    @ApiOperation("查询图库图片总数")
    @PutMapping
    public LuckResult findAllPhoto() {
        return photoService.findAllPhoto();
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public LuckResult delete(@RequestParam("idList") List<Long> idList) {
        return success(this.photoService.removeByIds(idList));
    }

    @ApiOperation("修改图片标签")
    @PutMapping("/modifyLabel")
    public LuckResult modifyLabel(@RequestBody Photo photo){
        if (photo.getId() != null && photo.getUserId()!=null && photo.getPhotoTag()!=null) {
            return photoService.modifyLabel(photo);
        }
        return LuckResult.error("修改信息错误");
    }

}

