package com.starmol.logicreview.repository.codereview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.logicreview.model.codereview.SourceCodeFileVersion;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SourceCodeFileVersionMapper extends BaseMapper<SourceCodeFileVersion> {

    @Select({"SELECT * FROM ljsc_file_version WHERE file_id = #{fileId}"})
    List<SourceCodeFileVersion> getAllFileVersionByFileId(Long fileId);
}
