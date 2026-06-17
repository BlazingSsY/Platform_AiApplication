package com.starmol.circuitreview.backend.repository.circuitreview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionVO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFileVersion;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CircuitFileVersionMapper extends BaseMapper<CircuitFileVersion> {

    @Select({"SELECT * FROM dlsc_file_version WHERE file_id = #{fileId}"})
    List<CircuitFileVersion> getAllFileVersionByFileId(Long fileId);

    /**
     * 根据文件ID查询文件版本列表（包含部门名称和所有者名称）
     *
     * @param fileId 文件ID
     * @return 文件版本VO列表
     */
    @Select({
            "<script>",
            "SELECT ",
            "    fv.id, ",
            "    fv.file_id, ",
            "    fv.minio_id as minioId, ",
            "    fv.file_version as fileVersion, ",
            "    fv.file_name as fileName, ",
            "    fv.file_origin_name as fileOriginName, ",
            "    fv.secret_level as secretLevel, ",
            "    fv.department_id as departmentId, ",
            "    d.name as departmentName, ",
            "    fv.owner_id as ownerId, ",
            "    u.name as ownerName, ",
            "    fv.is_recycle as isRecycle, ",
            "    fv.comments, ",
            "    fv.create_date as createDate, ",
            "    fv.update_date as updateDate, ",
            "    fv.create_user as createUser, ",
            "    fv.update_user as updateUser, ",
            "    fv.is_delete as isDelete, ",
            "    fv.version ",
            "FROM dlsc_file_version fv ",
            "LEFT JOIN urm_department d ON fv.department_id = d.id AND d.is_delete = 0 ",
            "LEFT JOIN urm_user u ON fv.owner_id = u.id AND u.is_delete = 0 ",
            "WHERE fv.file_id = #{fileId} ",
            "AND fv.is_recycle = 0 ",
            "AND fv.is_delete = 0 ",
            "ORDER BY fv.file_version DESC",
            "</script>"
    })
    List<CircuitFileVersionVO> getCircuitFileVersionVOListByFileId(Long fileId);
}
