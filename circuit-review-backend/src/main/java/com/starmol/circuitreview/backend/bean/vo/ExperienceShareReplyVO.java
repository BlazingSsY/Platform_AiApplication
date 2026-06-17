package com.starmol.circuitreview.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.model.base.BaseBean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "设计经验分享回复VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperienceShareReplyVO extends BaseBean implements Serializable {

    private static final long serialVersionUID = 8975418866807902002L;

    @Schema(description = "主记录ID")
    private Long fId;

    @Schema(description = "回复内容")
    private String reply;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "创建人")
    private Long createUser;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;

    @Schema(description = "附件文件列表")
    private List<AppendFileVO> appendFileList;
}
