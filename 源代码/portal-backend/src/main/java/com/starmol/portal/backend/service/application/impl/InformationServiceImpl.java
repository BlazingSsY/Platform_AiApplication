package com.starmol.portal.backend.service.application.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.portal.backend.bean.dto.InfoAppendFileDTO;
import com.starmol.portal.backend.bean.dto.InformationDTO;
import com.starmol.portal.backend.bean.vo.InformationVO;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.model.InfoAppendFile;
import com.starmol.portal.backend.model.Information;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.repository.InformationMapper;
import com.starmol.portal.backend.service.application.InfoAppendFileService;
import com.starmol.portal.backend.service.application.InformationService;
import com.starmol.portal.backend.service.base.impl.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InformationServiceImpl extends BaseServiceImpl<InformationMapper, Information> implements InformationService {
    private final InfoAppendFileService infoAppendFileService;
    private final ObjectMapper objectMapper;
    
    @Override
    public InformationVO createInformation(InformationDTO informationDTO) {
        Information information = objectMapper.convertValue(informationDTO, Information.class);
        Information savedInformation = this.saveAndReturnObject(information);
        if(CollectionUtils.isNotEmpty(informationDTO.getAppendFileList())) {
            List<InfoAppendFile> infoAppendFiles = new ArrayList<>();
            for (InfoAppendFileDTO infoAppendFileDTO : informationDTO.getAppendFileList()) {
                InfoAppendFile attachment = objectMapper.convertValue(infoAppendFileDTO, InfoAppendFile.class);
                attachment.setFId(savedInformation.getId());
                infoAppendFiles.add(attachment);
            }
            infoAppendFileService.saveBatch(infoAppendFiles);
        }
        return objectMapper.convertValue(savedInformation, InformationVO.class);
    }

    @Override
    public String deleteInformations(List<DeleteDTO> deleteObjects) {
        this.removeByIdsWithFill(deleteObjects);
        return deleteObjects.stream()
                .map(deleteDTO -> deleteDTO.getId().toString())
                .collect(Collectors.joining(","));
    }

    @Override
    public InformationVO updateInformation(Long id, InformationDTO informationDTO) {
        Information information = objectMapper.convertValue(informationDTO, Information.class);
        information.setId(id);
        Information updatedInformation = this.updateByIDAndReturnObject(information);
        return objectMapper.convertValue(updatedInformation, InformationVO.class);
    }

    @Override
    public Boolean changeStatus(Long id, Integer status) {
        Information information = new Information();
        information.setId(id);
        information.setStatus(status);
        return this.updateById(information);
    }

    @Override
    public IPage<InformationVO> getInformationListByPage(Long pageNumber, Long pageSize, String title) {
        IPage<Information> page = new Page<>(pageNumber, pageSize);
        IPage<Information> informationPage = this.page(
                page,
                Wrappers.<Information>lambdaQuery()
                        .like(StringUtils.isNotEmpty(title), Information::getTitle, title)
                        .orderByDesc(Information::getUpdateDate)
        );

        return informationPage.convert(information -> objectMapper.convertValue(information, InformationVO.class));
    }

    @Override
    public List<InformationVO> getInformationList() {
        List<Information> informationList = this.list(Wrappers.<Information>lambdaQuery().eq(Information::getStatus, 1).orderByDesc(Information::getUpdateDate));
        
        return informationList.stream()
                .map(info -> objectMapper.convertValue(info, InformationVO.class))
                .toList();
    }
    
    @Override
    public List<InformationVO> getInformationListByConfig(String rangeType, String startDate, String endDate, Integer count) {
        List<Information> informationList;
        
        // 根据不同的范围类型获取资讯列表
        switch (rangeType) {
            case "today":
                // 获取当天的资讯
                LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
                LocalDateTime endOfToday = startOfToday.plusDays(1);
                informationList = this.list(Wrappers.<Information>lambdaQuery()
                        .eq(Information::getStatus, 1)
                        .ge(Information::getCreateDate, startOfToday)
                        .lt(Information::getCreateDate, endOfToday)
                        .orderByDesc(Information::getUpdateDate));
                // 如果配置为当天但是没有获取到任何资讯，默认变成count
                if(CollectionUtils.isEmpty(informationList)) {
                    int limit = (count != null && count > 0) ? count : 10;
                    informationList = this.list(Wrappers.<Information>lambdaQuery()
                            .eq(Information::getStatus, 1)
                            .last("LIMIT " + limit)
                            .orderByDesc(Information::getUpdateDate));
                }
                break;
                
            case "range":
                // 获取指定日期范围的资讯
                LocalDateTime startDateTime = null;
                LocalDateTime endDateTime = null;
                
                try {
                    if (startDate != null && !startDate.isEmpty()) {
                        startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
                    }
                    
                    if (endDate != null && !endDate.isEmpty()) {
                        endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).plusDays(1).atStartOfDay();
                    }
                } catch (Exception e) {
                    // 如果日期解析失败，则使用默认值
                    startDateTime = LocalDate.now().minusDays(7).atStartOfDay(); // 默认最近一周
                    endDateTime = LocalDate.now().plusDays(1).atStartOfDay();
                }
                
                informationList = this.list(Wrappers.<Information>lambdaQuery()
                        .eq(Information::getStatus, 1)
                        .ge(startDateTime != null, Information::getCreateDate, startDateTime)
                        .lt(endDateTime != null, Information::getCreateDate, endDateTime)
                        .orderByDesc(Information::getUpdateDate));
                // 如果配置为日期范围但是没有获取到任何资讯，默认变成count
                if(CollectionUtils.isEmpty(informationList)) {
                    int limit = (count != null && count > 0) ? count : 10;
                    informationList = this.list(Wrappers.<Information>lambdaQuery()
                            .eq(Information::getStatus, 1)
                            .last("LIMIT " + limit)
                            .orderByDesc(Information::getUpdateDate));
                }
                break;
                
            case "count":
                // 获取指定数量的资讯
                int limit = (count != null && count > 0) ? count : 10;
                informationList = this.list(Wrappers.<Information>lambdaQuery()
                        .eq(Information::getStatus, 1)
                        .last("LIMIT " + limit)
                        .orderByDesc(Information::getUpdateDate));
                break;
                
            default:
                // 默认获取当天资讯
                LocalDateTime defaultStartOfToday = LocalDate.now().atStartOfDay();
                LocalDateTime defaultEndOfToday = defaultStartOfToday.plusDays(1);
                informationList = this.list(Wrappers.<Information>lambdaQuery()
                        .eq(Information::getStatus, 1)
                        .ge(Information::getCreateDate, defaultStartOfToday)
                        .lt(Information::getCreateDate, defaultEndOfToday)
                        .orderByDesc(Information::getUpdateDate));
                break;
        }
        
        return informationList.stream()
                .map(info -> objectMapper.convertValue(info, InformationVO.class))
                .toList();
    }

    @Override
    public InformationVO getInformationDetail(String id) {
        Information information = this.getOne(Wrappers.<Information>lambdaQuery().eq(Information::getId, id).eq(Information::getStatus, 1));
        if(Objects.isNull(information)) {
            throw new KnowException("资讯不存在");
        }
        return objectMapper.convertValue(information, InformationVO.class);
    }
}