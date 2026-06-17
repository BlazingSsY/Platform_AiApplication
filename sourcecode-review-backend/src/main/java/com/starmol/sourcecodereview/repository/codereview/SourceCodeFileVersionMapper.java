package com.starmol.sourcecodereview.repository.codereview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.sourcecodereview.model.codereview.SourceCodeFileVersion;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SourceCodeFileVersionMapper extends BaseMapper<SourceCodeFileVersion> {

    @Select({"SELECT * FROM dmsc_file_version WHERE file_id = #{fileId}"})
    List<SourceCodeFileVersion> getAllFileVersionByFileId(Long fileId);
}
