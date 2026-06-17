package com.starmol.circuitreview.backend.repository.circuitreview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailVO;
import com.starmol.circuitreview.backend.bean.vo.ReviewDetailRuleFilterDataVO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultDetail;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 电路审查结果详情Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface CircuitReviewResultDetailMapper extends BaseMapper<CircuitReviewResultDetail> {
    @Select({"<script>",
            "WITH latest_result_detail_audit AS (",
            "    SELECT DISTINCT ON (rrda.result_detail_id) ",
            "        rrda.id AS result_detail_audit_id, ",
            "        rrda.result_detail_id AS result_detail_id, ",
            "        rrda.reject_reason, ",
            "        rrda.status, ",
            "        rrda.audit_type, ",
            "        rrda.issue_feedback, ",
            "        rrda.audit_submit_time, ",
            "        rrda.audit_close_time, ",
            "        rrda.update_user ",
            "    FROM dlsc_review_result_detail_audit rrda ",
            "    ORDER BY rrda.result_detail_id, rrda.audit_submit_time DESC ",  // 按文件ID分组，取最高版本号的审查结果
            ") ",
            "SELECT d.id, ",
            "       d.result_id, ",
            "       d.rule_id, ",
            "       d.device_type, ",
            "       d.tag_pin, ",
            "       d.review_suggestion, ",
            "       d.is_passed, ",
            "       d.audit_type, ",
            "       d.approved_audit_type, ",
            "       d.issue_feedback, ",
            "       lrda.result_detail_audit_id, ",
            "       lrda.reject_reason, ",
            "       lrda.status AS audit_status, ",
            "       lrda.audit_submit_time, ",
            "       lrda.audit_close_time, ",
            "CASE WHEN lrda.status = 3 THEN lrda.audit_type ELSE NULL END AS reject_audit_type, ",
            "CASE WHEN lrda.status = 3 THEN lrda.issue_feedback ELSE NULL END AS reject_issue_feedback, ",
            "       u.id AS auditUserId, ",
            "       u.name AS audit_user_name, ",
            "       lrda.update_user, ",
            "CASE WHEN r1.id IS NOT NULL THEN r1.type ELSE r2.type END AS ruleType, ",
            "CASE WHEN r1.id IS NOT NULL THEN r1.name ELSE r2.name END AS ruleName, ",
            "CASE WHEN r1.id IS NOT NULL THEN r1.code ELSE r2.code END AS ruleCode ",
            "FROM dlsc_review_result_detail d ",
            "LEFT JOIN dlsc_rule r1 ON d.rule_id = r1.id ",
            "LEFT JOIN dlsc_rule r2 ON d.rule_code = r2.code AND r1.id IS NULL ",
            "LEFT JOIN latest_result_detail_audit lrda ON d.id = lrda.result_detail_id ",
            "LEFT JOIN urm_user u ON lrda.update_user = u.id ",
            "WHERE d.is_delete = 0 ",
            "<if test='resultId != null'> AND d.result_id = #{resultId} </if>",
            "<if test='ruleName != null and ruleName != \"\"'> AND (r1.name LIKE CONCAT('%', #{ruleName}, '%') OR r2.name LIKE CONCAT('%', #{ruleName}, '%')) </if>",
            "<if test='ruleType != null'> AND (r1.type = #{ruleType} OR r2.type = #{ruleType}) </if>",
            "<if test='emptyDeviceType == \"1\".toString()'> AND (TRIM(d.device_type) IS NULL or d.device_type IS NULL) </if>",
            "<if test='emptyDeviceType == \"0\".toString() and deviceType != null'> AND d.device_type= #{deviceType} </if>",
            "<if test='tagPin != null and tagPin != \"\"'> AND d.tag_pin LIKE CONCAT('%', #{tagPin}, '%') </if>",
            "<if test='reviewSuggestion != null and reviewSuggestion != \"\"'> AND d.review_suggestion LIKE CONCAT('%', #{reviewSuggestion}, '%') </if>",
            "<if test='isPassed != null'> AND d.is_passed = #{isPassed} </if>",
            "ORDER BY d.id",
            "</script>"
    })
    IPage<CircuitReviewResultDetailVO> selectDetailPageWithRule(
            IPage<CircuitReviewResultDetailVO> page,
            @Param("resultId") Long resultId,
            @Param("ruleName") String ruleName,
            @Param("ruleType") Integer ruleType,
            @Param("deviceType") String deviceType,
            @Param("tagPin") String tagPin,
            @Param("reviewSuggestion") String reviewSuggestion,
            @Param("isPassed") Integer isPassed,
            @Param("emptyDeviceType") Integer emptyDeviceType
    );

    @Select({"<script>",
            "SELECT CASE WHEN r1.id IS NOT NULL THEN r1.type ELSE r2.type END AS rule_type, ",
            "CASE WHEN r1.id IS NOT NULL THEN r1.name ELSE r2.name END AS review_rule, ",
            "d.device_type AS device_type, d.is_passed AS is_passed ",
            "FROM dlsc_review_result_detail d ",
            "LEFT JOIN dlsc_rule r1 ON d.rule_id = r1.id ",
            "LEFT JOIN dlsc_rule r2 ON d.rule_code = r2.code AND r1.id IS NULL ",
            "WHERE d.result_id = #{resultId} ",
            "ORDER BY d.update_date DESC ",
            "</script>"
    })
    List<ReviewDetailRuleFilterDataVO> getCircuitReviewResultDetailFilters(
            @Param("resultId") Long resultId
    );

    @Select({"<script>",
            "SELECT d.*, ",
            "CASE WHEN r1.id IS NOT NULL THEN r1.type ELSE r2.type END AS ruleType, ",
            "CASE WHEN r1.id IS NOT NULL THEN r1.name ELSE r2.name END AS ruleName ",
            "FROM dlsc_review_result_detail d ",
            "LEFT JOIN dlsc_rule r1 ON d.rule_id = r1.id ",
            "LEFT JOIN dlsc_rule r2 ON d.rule_code = r2.code AND r1.id IS NULL ",
            "WHERE d.is_delete = 0 ",
            "<if test='resultId != null'> AND d.result_id = #{resultId} </if>",
            "</script>"
    })
    List<CircuitReviewResultDetailVO> getAllDetailsByResultId(@Param("resultId") Long resultId);
}