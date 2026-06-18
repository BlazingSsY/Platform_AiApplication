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
@Schema(description = "设计经验分享DTO")
@Accessors(chain = true)
public class ExperienceShareDTO extends BaseBean implements Serializable {

    private static final long serialVersionUID = 8975418862356391102L;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "经验分享内容")
    private String content;

    @Schema(description = "贡献人")
    private String contributor;

    @Schema(description = "单位")
    private String organization;

    @Schema(description = "联系方式")
    private String contact;

    @Schema(description = "附件文件列表")
    private List<AppendFileDTO> appendFileList;
}
