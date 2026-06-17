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
@Schema(description = "设计经验分享VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperienceShareVO extends BaseBean implements Serializable {

    private static final long serialVersionUID = 8975418866807901556L;

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

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "当前用户是否已点赞")
    private Boolean currentUserLiked;

    @Schema(description = "创建人")
    private Long createUser;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "机构id")
    private Long departmentId;

    @Schema(description = "机构名称")
    private String departmentName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;

    @Schema(description = "附件文件列表")
    private List<AppendFileVO> appendFileList;
}
