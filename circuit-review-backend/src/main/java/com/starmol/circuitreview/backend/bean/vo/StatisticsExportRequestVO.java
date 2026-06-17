package com.starmol.circuitreview.backend.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StatisticsExportRequestVO {
    @Schema(description = "统计起始日期")
    private LocalDate startDate;

    @Schema(description = "统计截止日期")
    private LocalDate endDate;
}
