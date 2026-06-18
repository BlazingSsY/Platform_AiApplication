package com.starmol.logicreview.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.logicreview.bean.vo.ToolFileVO;
import com.starmol.logicreview.model.base.DeleteDTO;
import com.starmol.logicreview.model.codereview.ToolFile;
import com.starmol.logicreview.service.base.BaseCascadeService;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 工具文件服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface ToolFileService extends BaseCascadeService<ToolFile> {

    /**
     * 创建工具文件并返回VO
     */
    ToolFileVO createToolFile(MultipartFile file, String toolName, String comments);

    /**
     * 根据列表removeToolFiles 删除文件
     *
     * @param removeToolFiles 要删除的文件列表
     * @return 是否删除成功
     */
    void removeToolFilesWithTransaction(List<DeleteDTO> removeToolFiles);
    
    /**
     * 根据ID获取工具文件VO
     */
    ToolFileVO getToolFileVOById(Long id);
    

    /**
     * 分页查询工具文件
     */
    IPage<ToolFileVO> getToolFileVOPage(Long pageNumber,Long pageSize, String fileName);
}