package com.starmol.portal.backend.service.application.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.portal.backend.bean.dto.InfoAppendFileDTO;
import com.starmol.portal.backend.bean.vo.InfoAppendFileVO;
import com.starmol.portal.backend.model.InfoAppendFile;
import com.starmol.portal.backend.repository.InfoAppendFileMapper;
import com.starmol.portal.backend.service.application.InfoAppendFileService;
import com.starmol.portal.backend.service.base.impl.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InfoAppendFileServiceImpl extends BaseServiceImpl<InfoAppendFileMapper, InfoAppendFile> implements InfoAppendFileService {
    private final ObjectMapper objectMapper;
    
    @Override
    public List<InfoAppendFileVO> getInformationAttachments(Long infoId) {
        List<InfoAppendFile> attachments = this.list(
                Wrappers.<InfoAppendFile>lambdaQuery()
                        .eq(InfoAppendFile::getFId, infoId)
        );
        
        return attachments.stream()
                .map(attachment -> objectMapper.convertValue(attachment, InfoAppendFileVO.class))
                .collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public InfoAppendFileVO addInformationAttachment(Long infoId, InfoAppendFileDTO attachmentDTO) {
        InfoAppendFile attachment = objectMapper.convertValue(attachmentDTO, InfoAppendFile.class);
        attachment.setFId(infoId);
        InfoAppendFile savedAttachment = this.saveAndReturnObject(attachment);
        return objectMapper.convertValue(savedAttachment, InfoAppendFileVO.class);
    }

    @Override
    public void deleteInformationAttachment(String attachmentId) {
        this.removeById(attachmentId);
    }
}