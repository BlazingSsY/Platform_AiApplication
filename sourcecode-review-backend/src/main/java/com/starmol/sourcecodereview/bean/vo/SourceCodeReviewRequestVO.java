package com.starmol.sourcecodereview.bean.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SourceCodeReviewRequestVO {
    private Long fileVersionId;
} 