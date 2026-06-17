package com.starmol.circuitreview.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.starmol.circuitreview.backend.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "更新说明视图对象")
public class UpdateNoteVO extends BaseBean implements Serializable {

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "更新内容")
    private String content;
}
