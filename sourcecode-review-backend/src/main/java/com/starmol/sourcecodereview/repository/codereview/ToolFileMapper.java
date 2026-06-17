package com.starmol.sourcecodereview.repository.codereview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.sourcecodereview.model.codereview.ToolFile;

import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 工具文件Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface ToolFileMapper extends BaseMapper<ToolFile> {

    /**
     * 查询所有已逻辑删除的记录
     * @return 已删除的记录列表
     */
    @Select("SELECT * FROM dmsc_tool_file WHERE is_delete = 1")
    List<ToolFile> selectDeletedRecords();
} 