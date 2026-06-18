package com.starmol.portal.backend.service.application.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.portal.backend.bean.dto.ApplicationDTO;
import com.starmol.portal.backend.bean.vo.ApplicationVO;
import com.starmol.portal.backend.model.Application;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.repository.ApplicationMapper;
import com.starmol.portal.backend.service.application.ApplicationService;
import com.starmol.portal.backend.service.base.impl.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl extends BaseServiceImpl<ApplicationMapper, Application> implements ApplicationService {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public ApplicationVO createApplication(ApplicationDTO applicationDTO) {
        List<Application> applicationList = this.list(Wrappers.<Application>lambdaQuery().eq(Application::getModule, applicationDTO.getModule()));
        Application application = objectMapper.convertValue(applicationDTO, Application.class);
        application.setSequence(applicationList.size());
        Application savedApplication = this.saveAndReturnObject(application);
        return objectMapper.convertValue(savedApplication, ApplicationVO.class);
    }

    @Override
    public String deleteApplications(List<DeleteDTO> deleteObjects) {
        this.removeByIdsWithFill(deleteObjects);
        return deleteObjects.stream()
                .map(deleteDTO -> deleteDTO.getId().toString())
                .collect(Collectors.joining(","));
    }

    @Override
    public ApplicationVO updateApplication(Long id, ApplicationDTO applicationDTO) {
        Application application = objectMapper.convertValue(applicationDTO, Application.class);
        application.setId(id);
        Application updatedApplication = this.updateByIDAndReturnObject(application);
        return objectMapper.convertValue(updatedApplication, ApplicationVO.class);
    }

    @Override
    public Boolean changeStatus(Long id, Integer status) {
        Application application = new Application();
        application.setId(id);
        application.setStatus(status);
        return this.updateById(application);
    }

    @Override
    public IPage<ApplicationVO> getApplicationListByPage(Long pageNumber, Long pageSize, String name) {
        IPage<Application> page = new Page<>(pageNumber, pageSize);
        IPage<Application> applicationPage = this.page(
                page,
                Wrappers.<Application>lambdaQuery()
                        .like(StringUtils.isNotEmpty(name), Application::getName, name)
                        .orderByAsc(Application::getModule)
                        .orderByAsc(Application::getSequence)
                        .orderByDesc(Application::getCreateDate)
        );

        return applicationPage.convert(application -> objectMapper.convertValue(application, ApplicationVO.class));
    }

    @Override
    public Map<String, List<ApplicationVO>> getApplicationList() {
        List<Application> applications = this.list();
        List<ApplicationVO> applicationVOList = applications.stream()
                .map(app -> objectMapper.convertValue(app, ApplicationVO.class))
                .toList();
        
        // 定义模块顺序
        List<String> moduleOrder = List.of("设计研发", "运营管理", "生产制造", "算力资源");
        
        // 先按sequence排序，再按模块分组
        Map<String, List<ApplicationVO>> groupedByModule = applicationVOList.stream()
                .sorted((app1, app2) -> app1.getSequence().compareTo(app2.getSequence()))
                .collect(Collectors.groupingBy(
                        ApplicationVO::getModule,
                        LinkedHashMap::new,
                        Collectors.toList()));
        
        // 按照指定顺序重新排序模块
        return moduleOrder.stream()
                .filter(groupedByModule::containsKey)
                .collect(Collectors.toMap(
                        module -> module,
                        groupedByModule::get,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    @Override
    public ApplicationVO getApplicationDetail(String id) {
        Application application = this.getById(id);
        return objectMapper.convertValue(application, ApplicationVO.class);
    }
}