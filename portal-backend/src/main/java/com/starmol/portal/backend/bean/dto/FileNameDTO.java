package com.starmol.portal.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件对象操作DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileNameDTO implements Serializable {
    private static final long serialVersionUID = 5760034272814913520L;

    @Schema(description = "文件名称")
    private String fileName;

}