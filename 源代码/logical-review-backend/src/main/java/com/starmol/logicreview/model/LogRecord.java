package com.starmol.logicreview.model;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "操作日志")
@TableName(value = "urm_userlog")
public class LogRecord implements Serializable {

    private static final long serialVersionUID = -4406943328758680745L;

    @Schema(description = "唯一id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 操作具体内容描述
     */
    @Schema(description = "操作内容")
    private String content;

    /**
     * 操作模块
     */
    @Schema(description = "操作模块")
    private String modelName;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "操作时间")
    private LocalDateTime operateDate;

    /**
     * 操作者id
     */
    @Schema(description = "操作用户id")
    private Long userId;

    /**
     * 用户昵称
     */
    @Schema(description = "操作用户昵称")
    private String userName;

    /**
     * 登录名称
     */
    @Schema(description = "操作用户登录名")
    private String loginName;


}