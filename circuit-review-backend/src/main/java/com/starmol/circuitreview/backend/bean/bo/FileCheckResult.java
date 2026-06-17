package com.starmol.circuitreview.backend.bean.bo;

import lombok.Data;

@Data
public class FileCheckResult {
    private boolean isValid;

    private String expectedExtension;
}
