/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright STARMOL Advanced Technology Ltd. 2022-2023
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of STARMOL has been deposited with the P.R.China Copyright Office
 */
package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Yuexiaopeng
 * @description DirectoryVO
 * @since 2024/5/11 17:38
 */
@Data
@Schema(description = "目录VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectoryVO implements Serializable {

    private static final long serialVersionUID = 5430185666807901556L;
    @Schema(description = "目录名或文件名")
    private String name;

    @Schema(description = "目录")
    private String directoryName;

}
