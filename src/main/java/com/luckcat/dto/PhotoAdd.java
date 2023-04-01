package com.luckcat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Oriental
 * @version 1.0
 * @description TODO
 * @date 2023/3/31 16:09
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoAdd {
    private String userName;

    private String photoTag;
}
