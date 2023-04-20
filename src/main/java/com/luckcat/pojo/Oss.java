package com.luckcat.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("oss")
public class Oss {
    @TableId
    private Long id;
    private String name;
    private String url;
    @TableField("access_key")
    private String accessKey;
    @TableField("secret_key")
    private String secretKey;
    private String state;
}
