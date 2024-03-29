package com.luckcat.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId
    private Long uid;
    private String username;
    private String password;
    private String email;
    private String nickname;
    @TableField("avatar_address")
    private String avatarAddress;
    private String authority;
}
