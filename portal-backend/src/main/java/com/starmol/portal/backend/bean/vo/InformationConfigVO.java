package com.starmol.portal.backend.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "资讯配置VO")
public class InformationConfigVO implements Serializable {
    
    @Schema(description = "刷新时间间隔（秒），范围3-10，默认10")
    private Integer refreshInterval;
    
    @Schema(description = "资讯范围类型：today-当天，range-日期范围，count-条数")
    private String rangeType;
    
    @Schema(description = "开始日期（range模式下使用），格式：yyyy-MM-dd")
    private String startDate;
    
    @Schema(description = "结束日期（range模式下使用），格式：yyyy-MM-dd")
    private String endDate;
    
    @Schema(description = "获取条数（count模式下使用）")
    private Integer count;
}