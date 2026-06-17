package com.starmol.logicreview.service;

import com.starmol.logicreview.bean.vo.SourceCodeFileVersionAndResultVO;
import com.starmol.logicreview.bean.vo.SourceCodeFileVersionVO;
import com.starmol.logicreview.constant.FileSecretLevelEnum;
import com.starmol.logicreview.model.base.DeleteDTO;
import com.starmol.logicreview.model.codereview.SourceCodeFileVersion;
import com.starmol.logicreview.service.base.BaseService;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 代码文件版本服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeFileVersionService extends BaseService<SourceCodeFileVersion> {

    SourceCodeFileVersionVO createSourceCodeFileVersion(MultipartFile file, Long fileId, FileSecretLevelEnum fileSecretLevelEnum, String compatibleModels, String productModel, String productName, String configName, String codeFileVersion);
    
    SourceCodeFileVersionVO createSourceCodeFileVersion(String fileId, String fileName, String version, Long sourceCodeFileId, FileSecretLevelEnum fileSecretLevelEnum);

    List<SourceCodeFileVersionVO> getSourceCodeFileVersionVOById(Long fileId);

    SourceCodeFileVersionVO getSourceCodeFileVersionVOByVersionId(Long fileVersionId);

    List<SourceCodeFileVersionAndResultVO> getSourceCodeFileVersionAndResultVOById(Long fileId);

    void removeSourceCodeFilesWithTransaction(List<DeleteDTO> removeSourceCodeFiles);
    Map<String, String> getExportLink(String fileId, String fileName);
}
