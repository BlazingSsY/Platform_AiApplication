package com.starmol.logicreview.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改文件是否移入文件回收站的DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangeFileRecycleStatusDTO {
    @Schema(description = "修改文件ID列表")
    private List<Long> fileIdList;
    @Schema(description = "新的是否移入文件回收站(1:是; 0:否)")
    private Integer isRecycle;
}
