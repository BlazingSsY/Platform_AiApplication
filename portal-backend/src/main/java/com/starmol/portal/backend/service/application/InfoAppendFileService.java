package com.starmol.portal.backend.service.application;

import com.starmol.portal.backend.bean.dto.InfoAppendFileDTO;
import com.starmol.portal.backend.bean.vo.InfoAppendFileVO;
import com.starmol.portal.backend.model.InfoAppendFile;
import com.starmol.portal.backend.service.base.BaseService;

import java.util.List;

public interface InfoAppendFileService extends BaseService<InfoAppendFile> {
    /**
     * 根据资讯ID获取附件列表
     * @param infoId 资讯ID
     * @return 附件VO列表
     */
    List<InfoAppendFileVO> getInformationAttachments(Long infoId);

    /**
     * 添加资讯附件
     * @param infoId 资讯ID
     * @param attachmentDTO 附件信息DTO
     * @return 附件VO
     */
    InfoAppendFileVO addInformationAttachment(Long infoId, InfoAppendFileDTO attachmentDTO);

    /**
     * 删除资讯附件
     * @param attachmentId 附件ID
     */
    void deleteInformationAttachment(String attachmentId);
}