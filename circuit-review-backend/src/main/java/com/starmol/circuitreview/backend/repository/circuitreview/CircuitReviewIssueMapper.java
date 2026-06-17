package com.starmol.circuitreview.backend.repository.circuitreview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewIssue;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电路审查问题 Mapper 接口
 *
 * @author system
 * @date 2026-04-02
 */
@Mapper
public interface CircuitReviewIssueMapper extends BaseMapper<CircuitReviewIssue> {

}
