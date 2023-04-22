package com.luckcat.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Oriental
 * @version 1.0
 * @description 展示所有的user对象
 * @date 2023/4/3 13:58
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSelect {
    private String username;
    private String nickname;
    private String email;
    private String authority;
}
