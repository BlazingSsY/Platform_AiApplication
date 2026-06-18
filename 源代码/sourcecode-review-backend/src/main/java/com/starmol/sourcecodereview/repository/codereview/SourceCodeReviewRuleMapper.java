package com.starmol.sourcecodereview.repository.codereview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.sourcecodereview.model.codereview.SourceCodeReviewRule;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

/**
 * 电路审查规则Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeReviewRuleMapper extends BaseMapper<SourceCodeReviewRule> {
    
    /**
     * 物理删除所有规则数据
     * 注意：这是物理删除，不是软删除
     * 
     * @return 删除的记录数
     */
    @Delete("DELETE FROM dmsc_rule")
    int deleteAllRulesPhysically();
    
    /**
     * 根据条件物理删除规则数据
     * 
     * @param comments 备注条件，用于删除特定来源的规则
     * @return 删除的记录数
     */
    @Delete("DELETE FROM dmsc_rule WHERE comments = #{comments}")
    int deleteRulesByCommentsPhysically(@Param("comments") String comments);
    
    /**
     * 根据ID物理删除规则数据
     * 
     * @param id 规则ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM dmsc_rule WHERE id = #{id}")
    int deleteByIdPhysically(@Param("id") Long id);
} 