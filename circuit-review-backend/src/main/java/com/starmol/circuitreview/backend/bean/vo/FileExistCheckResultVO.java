package com.starmol.circuitreview.backend.bean.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileExistCheckResultVO {
    private Boolean exist;
    private Boolean conflictWithRecycledFile;
    private Boolean inAudit;
    private Long fileId;
}
