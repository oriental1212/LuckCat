package com.luckcat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Oriental
 * @version 1.0
 * @description 用户修改个人资料实体类
 * @date 2023/4/8 12:39
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRevise {
    private String email;
    private String nickname;
}
