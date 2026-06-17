package com.starmol.circuitreview.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "更新说明数据传输对象")
public class UpdateNoteDTO implements Serializable {

    @NotNull(message = "更新时间不能为空")
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @NotBlank(message = "更新内容不能为空")
    @Schema(description = "更新内容")
    private String content;
}
