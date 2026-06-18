package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.NamingConventionUpdateDTO;
import com.starmol.circuitreview.backend.bean.vo.NamingConventionVO;
import com.starmol.circuitreview.backend.model.circuitreview.NamingConvention;
import com.starmol.circuitreview.backend.repository.circuitreview.NamingConventionMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.NamingConventionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@Slf4j
@RequiredArgsConstructor
public class NamingConventionServiceImpl extends BaseCascadeServiceImpl<NamingConventionMapper, NamingConvention> implements NamingConventionService {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public NamingConventionVO createNamingConvention(NamingConvention namingConvention) {
        NamingConvention savedNamingConvention = this.saveAndReturnObject(namingConvention);
        return objectMapper.convertValue(savedNamingConvention, NamingConventionVO.class);
    }
    
    @Override
    public IPage<NamingConventionVO> getNamingConventionVOPage(Page<NamingConvention> page) {
        IPage<NamingConvention> namingConventionPage = this.page(page);
        return namingConventionPage.convert(namingConvention ->
            objectMapper.convertValue(namingConvention, NamingConventionVO.class));
    }
    
    @Override
    public NamingConventionVO updateNamingConvention(Long id, @Valid NamingConventionUpdateDTO namingConventionUpdateDTO) {
        NamingConvention namingConvention = new NamingConvention();
        namingConvention.setId(id);
        namingConvention.setTitle(namingConventionUpdateDTO.getTitle());
        namingConvention.setContent(namingConventionUpdateDTO.getContent());
        NamingConvention updatedNamingConvention = this.updateByIDAndReturnObject(namingConvention);
        return objectMapper.convertValue(updatedNamingConvention, NamingConventionVO.class);
    }
    
    @Override
    public NamingConventionVO getNamingConventionVOById(Long id) {
        NamingConvention namingConvention = this.getById(id);
        if (namingConvention == null) {
            return null;
        }
        return objectMapper.convertValue(namingConvention, NamingConventionVO.class);
    }
}