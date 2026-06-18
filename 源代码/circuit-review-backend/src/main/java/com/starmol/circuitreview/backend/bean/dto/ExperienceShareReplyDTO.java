package com.starmol.circuitreview.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.model.base.BaseBean;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "设计经验分享回复DTO")
@Accessors(chain = true)
public class ExperienceShareReplyDTO extends BaseBean implements Serializable {
    private static final long serialVersionUID = 8975418862356392002L;

    @Schema(description = "主记录ID")
    private Long fId;

    @Schema(description = "回复内容")
    private String reply;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "附件文件列表")
    private List<AppendFileDTO> appendFileList;
}
