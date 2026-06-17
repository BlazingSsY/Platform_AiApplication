package com.starmol.logicreview.bean.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SourceCodeReviewRequestVO {
    private Long fileVersionId;
} 