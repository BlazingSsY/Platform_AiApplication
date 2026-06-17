package com.starmol.portal.backend.service.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.dto.ApplicationDTO;
import com.starmol.portal.backend.bean.vo.ApplicationVO;
import com.starmol.portal.backend.bean.vo.SimpleItemVO;
import com.starmol.portal.backend.model.Application;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.base.BaseService;

import java.util.List;
import java.util.Map;

public interface ApplicationService extends BaseService<Application> {
    /**
     * 创建应用
     * @param applicationDTO 应用信息DTO
     * @return 应用VO
     */
    ApplicationVO createApplication(ApplicationDTO applicationDTO);

    /**
     * 删除应用
     * @param deleteObjects 删除对象列表
     * @return 删除的应用ID列表
     */
    String deleteApplications(List<DeleteDTO> deleteObjects);

    /**
     * 更新应用
     * @param id 应用ID
     * @param applicationDTO 应用信息DTO
     * @return 应用VO
     */
    ApplicationVO updateApplication(Long id, ApplicationDTO applicationDTO);

    /**
     * 修改应用状态
     * @param id 应用ID
     * @param status 新状态
     * @return 应用VO
     */
    Boolean changeStatus(Long id, Integer status);

    /**
     * 分页查询应用
     * @param pageNumber 页码
     * @param pageSize 每页条数
     * @param name 应用名称
     * @return 应用VO分页数据
     */
    IPage<ApplicationVO> getApplicationListByPage(Long pageNumber, Long pageSize, String name);

    /**
     * 获取应用列表
     * @return 应用简要信息列表
     */
    Map<String, List<ApplicationVO>> getApplicationList();

    /**
     * 根据ID获取应用详情
     * @param id 应用ID
     * @return 应用VO
     */
    ApplicationVO getApplicationDetail(String id);
}