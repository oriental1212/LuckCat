package com.luckcat.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Oriental
 * @version 1.0
 * @description Setting实体类
 * @date 2023/4/11 14:06
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("setting")
public class Setting {
    @TableId(value = "setting_id",type = IdType.AUTO)
    private Integer settingId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("user_id")
    private Long userId;
    @TableField("storage_space")
    private String storageSpace;
    @TableField("storage_size")
    private String storageSize;
    @TableField("storage_quantity")
    private String storageQuantity;
    @TableField("storage_used")
    private String storageUsed;
}
