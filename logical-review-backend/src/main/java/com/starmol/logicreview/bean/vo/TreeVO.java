package com.starmol.logicreview.bean.vo;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "获服务树结果")
public class TreeVO {

    @Schema(description = "父节点id")
    private Long fid;

    @Schema(description = "子服务列表")
    private List<TreeVO> children;
}
