package com.starmol.circuitreview.backend.repository.circuitreview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultDetailAuditVO;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultDetailAudit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 电路审查结果详情审核数据访问层
 */
@Mapper
public interface CircuitReviewResultDetailAuditMapper extends BaseMapper<CircuitReviewResultDetailAudit> {
    
    @Select("<script>" +
            "SELECT " +
            "    a.id, " +
            "    a.file_id, " +
            "    a.file_version_id, " +
            "    a.result_id, " +
            "    a.result_detail_id, " +
            "    a.status, " +
            "    a.audit_submit_time, " +
            "    a.audit_close_time, " +
            "    drrd.rule_id, " +
            "    drrd.rule_code, " +
            "    drrd.device_type, " +
            "    drrd.tag_pin, " +
            "    drrd.review_suggestion, " +
            "    drrd.is_passed, " +
            "    a.audit_type, " +
            "CASE WHEN a.status IN (2, 3) AND a.update_user != a.create_user THEN u.name ELSE NULL END AS audit_user_name, " +
            "    drrd.approved_audit_type, " +
            "    a.issue_feedback, " +
            "    a.reject_reason, " +
            "    r.type AS rule_type, " +
            "    r.name AS rule_name " +
            "FROM dlsc_review_result_detail_audit a " +
            "LEFT JOIN dlsc_review_result_detail drrd ON a.result_detail_id = drrd.id and drrd.is_delete=0 " +
            "LEFT JOIN dlsc_rule r ON drrd.rule_id = r.id " +
            "LEFT JOIN urm_user u ON a.update_user = u.id " +
            "    <where> " +
            "        a.result_audit_id = #{resultAuditId} AND a.is_delete = 0 " +
            "        <if test='auditTypeEnumList != null and auditTypeEnumList.size() > 0'> " +
            "            AND a.audit_type IN " +
            "            <foreach collection='auditTypeEnumList' item='auditType' open='(' separator=',' close=')'> " +
            "              #{auditType} " +
            "            </foreach> " +
            "        </if> " +
            "    </where> " +
            "    ORDER BY drrd.id, a.audit_submit_time DESC " +
            "</script>")
    IPage<CircuitReviewResultDetailAuditVO> getCircuitReviewResultDetailAuditVOPage(Page<CircuitReviewResultDetailAudit> page, Long resultAuditId, List<CircuitReviewResultDetailAuditTypeEnum> auditTypeEnumList);
}