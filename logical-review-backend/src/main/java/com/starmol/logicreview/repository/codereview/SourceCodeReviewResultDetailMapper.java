package com.starmol.logicreview.repository.codereview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.logicreview.bean.bo.SourceCodeReviewResultDetailRuleBO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultDetailVO;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResultDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 代码审查结果详情Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeReviewResultDetailMapper extends BaseMapper<SourceCodeReviewResultDetail> {
    @Select({"<script>",
        "SELECT d.*, r.type AS ruleType, r.name AS ruleName, r.code AS ruleCode  ",
        "FROM ljsc_review_result_detail d ",
        "LEFT JOIN ljsc_rule r ON d.rule_id = r.id ",
        "WHERE d.is_delete = 0 ",
        "<if test='resultId != null'> AND d.result_id = #{resultId} </if>",
        "<if test='ruleName != null and ruleName != \"\"'> AND r.name LIKE CONCAT('%', #{ruleName}, '%') </if>",
        "<if test='ruleType != null and ruleType != \"\"'> AND r.type LIKE CONCAT('%', #{ruleType}, '%') </if>",
        "<if test='sourceFileName != null'> AND d.source_file_name LIKE CONCAT('%', #{sourceFileName}, '%') </if>",
        "<if test='lineNumber != null'> AND d.line_number LIKE CONCAT('%', #{lineNumber}, '%') </if>",
        "<if test='errorCode != null'> AND d.error_code LIKE CONCAT('%', #{errorCode}, '%') </if>",
        "<if test='errorReason != null'> AND d.error_reason LIKE CONCAT('%', #{errorReason}, '%') </if>",
        "<if test='reviewSuggestion != null and reviewSuggestion != \"\"'> AND d.review_suggestion LIKE CONCAT('%', #{reviewSuggestion}, '%') </if>",
        "<if test='isPassed != null'> AND d.is_passed = #{isPassed} </if>",
        " ORDER BY d.is_passed ",
        "</script>"
    })
    IPage<SourceCodeReviewResultDetailVO> selectDetailWithRule(
        IPage<SourceCodeReviewResultDetailVO> page,
        @Param("resultId") Long resultId,
        @Param("ruleName") String ruleName,
        @Param("ruleType") String ruleType,
        @Param("sourceFileName") String sourceFileName,
        @Param("lineNumber") String lineNumber,
        @Param("errorCode") String errorCode,
        @Param("errorReason") String errorReason,
        @Param("reviewSuggestion") String reviewSuggestion,
        @Param("isPassed") Integer isPassed,
        @Param("emptySourceFileName") Integer emptySourceFileName
    );

    @Select({"<script>",
        "SELECT d.*, r.type AS ruleType, r.name AS ruleName, r.code AS ruleCode  ",
        "FROM ljsc_review_result_detail d ",
        "LEFT JOIN ljsc_rule r ON d.rule_id = r.id ",
        "WHERE d.is_delete = 0 ",
        "<if test='resultId != null'> AND d.result_id = #{resultId} </if>",
        "<if test='ruleName != null and ruleName != \"\"'> AND r.name LIKE CONCAT('%', #{ruleName}, '%') </if>",
        "<if test='ruleType != null and ruleType != \"\"'> AND r.type LIKE CONCAT('%', #{ruleType}, '%') </if>",
        "<if test='sourceFileName != null'> AND d.source_file_name LIKE CONCAT('%', #{sourceFileName}, '%') </if>",
        "<if test='lineNumber != null'> AND d.line_number LIKE CONCAT('%', #{lineNumber}, '%') </if>",
        "<if test='errorCode != null'> AND d.error_code LIKE CONCAT('%', #{errorCode}, '%') </if>",
        "<if test='errorReason != null'> AND d.error_reason LIKE CONCAT('%', #{errorReason}, '%') </if>",
        "<if test='reviewSuggestion != null and reviewSuggestion != \"\"'> AND d.review_suggestion LIKE CONCAT('%', #{reviewSuggestion}, '%') </if>",
        "<if test='isPassed != null'> AND d.is_passed = #{isPassed} </if>",
        "</script>"
    })
    List<SourceCodeReviewResultDetailVO> selectDetailWithRule(
        @Param("resultId") Long resultId,
        @Param("ruleName") String ruleName,
        @Param("ruleType") String ruleType,
        @Param("sourceFileName") String sourceFileName,
        @Param("lineNumber") String lineNumber,
        @Param("errorCode") String errorCode,
        @Param("errorReason") String errorReason,
        @Param("reviewSuggestion") String reviewSuggestion,
        @Param("isPassed") Integer isPassed,
        @Param("emptySourceFileName") Integer emptySourceFileName
    );

    @Select({"<script>",
        "SELECT DISTINCT d.source_file_name",
        "FROM ljsc_review_result_detail d ",
        "WHERE d.result_id = #{resultId} ",
        "<if test='isPassed != null'> AND is_passed = #{isPassed} </if>",
        "</script>"
    })
    List<String> getSourceCodeReviewResultFilters(
        @Param("resultId") Long resultId,
        @Param("isPassed") Integer isPassed
    );

    @Select({"<script>",
            "SELECT r.name FROM ( SELECT DISTINCT ON (rule_id) rule_id, is_passed FROM ljsc_review_result_detail ",
            "WHERE result_id = #{resultId} ",
            "<if test='isPassed != null'> AND is_passed = #{isPassed} </if>",
            ") d ",
            "LEFT JOIN ljsc_rule r ON d.rule_id = r.id ",
            "</script>"
    })
    List<String> getSourceCodeReviewResultRuleFilters(
            @Param("resultId") Long resultId,
            @Param("isPassed") Integer isPassed
    );



    @Select({"<script>",
        "SELECT d.*, r.type AS ruleType, r.name AS ruleName ",
        "FROM ljsc_review_result_detail d ",
        "LEFT JOIN ljsc_rule r ON d.rule_id = r.id ",
        "WHERE d.is_delete = 0 ",
        "<if test='resultId != null'> AND d.result_id = #{resultId} </if>",
        "</script>"
    })
    List<SourceCodeReviewResultDetailVO> getAllDetailsByResultId(@Param("resultId") Long resultId);


    @Select({"<script>",
            "SELECT d.source_file_name, r.type AS ruleType, r.name AS ruleName, r.code AS ruleCode",
            "FROM ljsc_review_result_detail d ",
            "LEFT JOIN ljsc_rule r ON d.rule_id = r.id ",
            "WHERE d.is_delete = 0 ",
            "<if test='resultId != null'> AND d.result_id = #{resultId} </if>",
            "</script>"
    })
    List<SourceCodeReviewResultDetailRuleBO> getAllDetailsAndRuleByResultId(@Param("resultId") Long resultId);

} 