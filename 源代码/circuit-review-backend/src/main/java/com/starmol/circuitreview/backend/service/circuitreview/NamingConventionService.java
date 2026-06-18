package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.NamingConventionUpdateDTO;
import com.starmol.circuitreview.backend.bean.vo.NamingConventionVO;
import com.starmol.circuitreview.backend.model.circuitreview.NamingConvention;
import com.starmol.circuitreview.backend.service.base.BaseCascadeService;

import javax.validation.Valid;

public interface NamingConventionService extends BaseCascadeService<NamingConvention> {
    /**
     * 创建命名规范
     * @param namingConvention 命名规范实体
     * @return 命名规范VO
     */
    NamingConventionVO createNamingConvention(NamingConvention namingConvention);
    
    /**
     * 分页查询命名规范
     * @param page 分页参数
     * @return 命名规范分页结果
     */
    IPage<NamingConventionVO> getNamingConventionVOPage(Page<NamingConvention> page);
    
    /**
     * 根据ID更新命名规范
     * @param id 命名规范ID
     * @param namingConventionUpdateDTO 命名规范实体
     * @return 命名规范VO
     */
    NamingConventionVO updateNamingConvention(Long id, @Valid NamingConventionUpdateDTO namingConventionUpdateDTO);
    
    /**
     * 根据ID获取命名规范
     * @param id 命名规范ID
     * @return 命名规范VO
     */
    NamingConventionVO getNamingConventionVOById(Long id);
}