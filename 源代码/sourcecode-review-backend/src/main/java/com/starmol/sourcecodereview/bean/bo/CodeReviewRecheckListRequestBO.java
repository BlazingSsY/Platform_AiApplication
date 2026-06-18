package com.starmol.sourcecodereview.bean.bo;


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
public class CodeReviewRecheckListRequestBO implements Serializable {
    private static final long serialVersionUID = 221863112103524707L;

    @Schema(description =  "reviewID列表")
    private List<String> reviewIds;

    @Schema(description =  "用户列表")
    private List<String> submitUserIds;

    @Schema(description = "状态")
    private Integer recheckStatus;

    @Schema(description = "用户角色类型")
    private Integer curUserRoleType;

    @Schema(description = "页大小")
    private Integer pageSize;

    @Schema(description = "页号")
    private Integer pageNum;
}
