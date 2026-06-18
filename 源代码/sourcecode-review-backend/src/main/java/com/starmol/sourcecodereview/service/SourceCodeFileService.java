package com.starmol.sourcecodereview.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.sourcecodereview.bean.bo.PulverizeFileResultBO;
import com.starmol.sourcecodereview.bean.dto.ChangeFileRecycleStatusDTO;
import com.starmol.sourcecodereview.bean.dto.SourceCodeFileDTO;
import com.starmol.sourcecodereview.bean.vo.FileExistCheckResultVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileDetailVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileVO;
import com.starmol.sourcecodereview.constant.FileSecretLevelEnum;
import com.starmol.sourcecodereview.model.base.DeleteDTO;
import com.starmol.sourcecodereview.model.codereview.SourceCodeFile;
import com.starmol.sourcecodereview.service.base.BaseCascadeService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 代码文件服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeFileService extends BaseCascadeService<SourceCodeFile> {

    /**
     * 创建代码文件
     *
     * @param sourceCodeFileDTO 代码文件DTO
     * @return 代码文件VO
     */
    SourceCodeFileVO createSourceCodeFile(SourceCodeFileDTO sourceCodeFileDTO);

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 文件存在检查结果VO
     */
    FileExistCheckResultVO checkFileExist(String fileName);

    /**
     * 创建代码文件(直接上传文件)
     *
     * @param file 文件
     * @param fileSecretLevelEnum 文件密级
     * @return 代码文件VO
     */
    SourceCodeFileVO createSourceCodeFile(MultipartFile file, FileSecretLevelEnum fileSecretLevelEnum, String compatibleModels, String productModel, String productName, String configName, String codeFileVersion, Integer isTestFile);

    /**
     * 根据列表removeFiles 删除文件
     * @param removeFiles 要删除的文件列表
     */
    PulverizeFileResultBO removeSourceCodeFilesWithTransaction(DeleteDTO removeFiles);

    /**
     * 更新代码文件
     *
     * @param id 代码文件ID
     * @param sourceCodeFile 代码文件
     * @return 代码文件VO
     */
    SourceCodeFileVO updateSourceCodeFile(Long id, SourceCodeFile sourceCodeFile);

    /**
     * 变更代码文件回收站状态
     *
     * @param changeFileRecycleStatusDTO 是否移入回收站
     * @return 是否成功
     */
    Boolean changeSourceCodeFileRecycleStatus(ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO);

    /**
     * 根据ID获取代码文件VO
     *
     * @param id 代码文件ID
     * @return 代码文件VO
     */
    SourceCodeFileVO getSourceCodeFileVOById(Long id);

    /**
     * 根据文件名获取代码文件VO
     *
     * @param fileName 文件名
     * @return 代码文件VO
     */
    SourceCodeFileVO getSourceCodeFileVOByFileName(String fileName);

    /**
     * 获取回收站代码文件分页
     *
     * @param page 分页参数
     * @param fileName 文件名
     * @return 代码文件VO分页
     */
    IPage<SourceCodeFileVO> getRecycledSourceCodeFileVOPage(Page<SourceCodeFile> page, String fileName);

    IPage<SourceCodeFileDetailVO> getSourceCodeFileDetailVOPage(Page<SourceCodeFileDetailVO> page, Long depId, Long userId, String fileName);
}