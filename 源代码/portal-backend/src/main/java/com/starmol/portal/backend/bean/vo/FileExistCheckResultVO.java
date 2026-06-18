package com.starmol.portal.backend.bean.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileExistCheckResultVO {
    private Boolean exist;
    private Long fileId;
}
