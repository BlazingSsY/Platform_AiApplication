package com.starmol.logicreview.model.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "对象隶属关系操作DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelationDTO extends DeleteDTO implements Serializable {
    private static final long serialVersionUID = 5760034272814953520L;

    @Schema(description = "隶属对象ID")
    private Long ownerId;

}