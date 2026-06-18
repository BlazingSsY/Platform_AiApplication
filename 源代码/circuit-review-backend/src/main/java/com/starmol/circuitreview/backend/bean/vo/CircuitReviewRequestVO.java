package com.starmol.circuitreview.backend.bean.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CircuitReviewRequestVO {
    private Long fileVersionId;
}
