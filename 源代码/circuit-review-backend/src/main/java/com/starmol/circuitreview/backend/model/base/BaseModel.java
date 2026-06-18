package com.starmol.circuitreview.backend.model.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;
import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE;

@Data
public class BaseModel implements Serializable {
    @Schema(description = "唯一id")
    @TableId(type = IdType.INPUT)
    private Long id;
    /**
     * 逻辑删除的状态 1: 已删除  0: 正常
     */
    @Schema(description = "是否已删除", hidden = true)
    @TableLogic
    @TableField(select = false)
    private Integer isDelete;
    @Version
    private Integer version;
    @Schema(description = "创建人")
    @TableField(fill = INSERT)
    private Long createUser;
    @Schema(description = "更新人")
    @TableField(fill = INSERT_UPDATE)
    private Long updateUser;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = INSERT)
    private LocalDateTime createDate;
    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = INSERT_UPDATE)
    private LocalDateTime updateDate;
}