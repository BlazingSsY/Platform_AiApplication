package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.ChangeFileRecycleStatusDTO;
import com.starmol.circuitreview.backend.bean.dto.CircuitFileDTO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileDetailVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVO;
import com.starmol.circuitreview.backend.bean.vo.FileExistCheckResultVO;
import com.starmol.circuitreview.backend.constant.CircuitFileSortFieldEnum;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.constant.SortDirectionEnum;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFile;
import com.starmol.circuitreview.backend.service.base.BaseCascadeService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 电路图文件服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface CircuitFileService extends BaseCascadeService<CircuitFile> {
    
    /**
     * 创建电路图文件并返回VO
     */
    CircuitFileVO createCircuitFile(CircuitFileDTO circuitFileDTO);

    /**
     * 检查当前用户下的同名文件是否存在
     */
    FileExistCheckResultVO checkFileExist(String fileName);

    /**
     * 创建电路图文件并返回VO
     */
    CircuitFileVO createCircuitFile(MultipartFile file, FileSecretLevelEnum fileSecretLevelEnum, String compatibleModels, String productModel, String productName, String diagramNumber, String diagramVersion, Integer isTestFile);

    /**
     * 根据列表removeCircuitFiles 删除文件
     *
     * @param removeCircuitFiles 要删除的文件列表
     */
    void removeCircuitFilesWithTransaction(List<DeleteDTO> removeCircuitFiles);
    
    /**
     * 更新电路图文件并返回VO
     */
    CircuitFileVO updateCircuitFile(Long id, CircuitFile circuitFile);

    /**
     * 修改文件“是否逻辑删除”状态
     */
    Boolean changeCircuitFileRecycle(ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO);
    
    /**
     * 根据ID获取电路图文件VO
     */
    CircuitFileVO getCircuitFileVOById(Long id);

    /**
     * 根据文件名称获取当前用户的电路图文件VO
     */
    CircuitFileVO getCircuitFileVOByFileName(String fileName);
    
    /**
     * 分页查询回收站中的电路图文件VO
     */
    IPage<CircuitFileDetailVO> getRecycledCircuitFileVOPage(Page<CircuitFile> page, Long depId, Long userId, String fileName);

    /**
     * 分页查询电路图文件详情，包括最新的审查结果
     */
    IPage<CircuitFileDetailVO> getCircuitFileDetailVOPage(Page<CircuitFileDetailVO> page, Long depId, Long userId, String fileName, CircuitFileSortFieldEnum sortField, SortDirectionEnum sortDirection);

    void removeCircuitFileReviewResult(List<DeleteDTO> removeObjects);

    FileExistCheckResultVO checkCircuitFileInAudit(Long fileId);
}