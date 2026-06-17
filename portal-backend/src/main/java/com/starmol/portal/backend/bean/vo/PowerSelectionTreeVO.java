package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "可选权限树结构信息VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerSelectionTreeVO extends PowerSelectionVO implements Serializable {
    /**
     * 子节点列表
     */
    @Schema(description = "子节点列表")
    private List<PowerSelectionTreeVO> childList;
}