package com.starmol.circuitreview.backend.repository.circuitreview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileDetailVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewHomeStatisticsDataItemVO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResult;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 电路审查结果Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface CircuitReviewResultMapper extends BaseMapper<CircuitReviewResult> {

    @Select({"<script>",
            "SELECT * FROM dlsc_review_result WHERE id IN (SELECT DISTINCT(result_id) FROM dlsc_review_result_detail WHERE rule_id NOT IN (SELECT id FROM dlsc_rule)) AND is_closed_loop = 0 AND is_delete = 0 ORDER BY review_time ",
            "</script>"
    })
    List<CircuitReviewResult> getInvalidReviewResultIds();

    /**
     * 根据文件ID列表查询每个文件的最大检查点数
     *
     * @param fileIds 文件ID列表
     * @return 包含file_id和max_check_points的映射列表
     */
    @Select({"<script>",
            "SELECT file_id, MAX(check_points) as max_check_points ",
            "FROM dlsc_review_result ",
            "WHERE file_id IN ",
            "<foreach collection='fileIds' item='fileId' open='(' separator=',' close=')'> ",
            "  #{fileId} ",
            "</foreach> ",
            "GROUP BY file_id ",
            "</script>"
    })
    List<Map<String, Object>> selectMaxCheckPointsByFileIds(@Param("fileIds") List<Long> fileIds);
}