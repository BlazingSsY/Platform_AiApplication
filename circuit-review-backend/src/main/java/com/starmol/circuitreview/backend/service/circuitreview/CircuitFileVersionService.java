package com.starmol.circuitreview.backend.service.circuitreview;

import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionAndResultVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionVO;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFileVersion;
import com.starmol.circuitreview.backend.service.base.BaseCascadeService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CircuitFileVersionService extends BaseCascadeService<CircuitFileVersion> {
    CircuitFileVersionVO createCircuitFileVersion(MultipartFile file, Long fileId, FileSecretLevelEnum fileSecretLevelEnum);

    CircuitFileVersionVO createCircuitFileVersion(String minioId, String fileName, String fileOriginName, Long fileId, FileSecretLevelEnum fileSecretLevelEnum);

    List<CircuitFileVersionVO> getCircuitFileVersionVOById(Long fileId);

    CircuitFileVersionVO getCircuitFileVersionVOByVersionId(Long fileVersionId);

    List<CircuitFileVersionAndResultVO> getCircuitFileVersionAndResultVOById(Long fileId);

    void removeCircuitFilesWithTransaction(List<DeleteDTO> removeCircuitFiles);
}
