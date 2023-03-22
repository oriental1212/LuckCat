package com.luckcat.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("photo")
public class Photo {
    @TableId
    private Integer id;
    @TableField("photo_name")
    private String photoName;
    @TableField("user_id")
    private Long userId;
    @TableField("photo_type")
    private String photoType;
    @TableField("photo_tag")
    private String photoTag;
    @TableField("photo_url")
    private String photoUrl;
    @TableField("photo_creat_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date photoCreatTime;
}
