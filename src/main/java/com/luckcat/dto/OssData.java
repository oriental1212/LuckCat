package com.luckcat.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Oriental
 * @version 1.0
 * @description TODO
 * @date 2023/4/20 14:58
 */

@Data
public class OssData {
    private String ossName;
    private List<String> ossBucketNames;
    private String ossState;
}
