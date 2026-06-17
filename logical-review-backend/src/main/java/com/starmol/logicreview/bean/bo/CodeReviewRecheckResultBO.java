package com.starmol.logicreview.bean.bo;


import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "存放调用第三方服务请求数据")
public class CodeReviewRecheckResultBO implements Serializable {
    private static final long serialVersionUID = 221863111103524707L;

    @Schema(description = "记录列表")
    private List<CodeReviewRecheckRecoderBO> records;

    @Schema(description = "总数")
    private Integer total;

    @Schema(description = "页码")
    private Integer pageNum;

    @Schema(description = "页大小")
    private Integer pageSize;

    @Schema(description = "总页数")
    private Integer totalPage;
}
