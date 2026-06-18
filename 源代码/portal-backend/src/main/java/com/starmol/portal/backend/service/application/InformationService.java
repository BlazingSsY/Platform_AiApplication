package com.starmol.portal.backend.service.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.dto.InformationDTO;
import com.starmol.portal.backend.bean.vo.InfoAppendFileVO;
import com.starmol.portal.backend.bean.vo.InformationVO;
import com.starmol.portal.backend.bean.vo.SimpleItemVO;
import com.starmol.portal.backend.model.InfoAppendFile;
import com.starmol.portal.backend.model.Information;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.base.BaseService;

import java.time.LocalDate;
import java.util.List;

public interface InformationService extends BaseService<Information> {
    /**
     * 创建资讯
     * @param informationDTO 资讯信息DTO
     * @return 资讯VO
     */
    InformationVO createInformation(InformationDTO informationDTO);

    /**
     * 删除资讯
     * @param deleteObjects 删除对象列表
     * @return 删除的资讯ID列表
     */
    String deleteInformations(List<DeleteDTO> deleteObjects);

    /**
     * 更新资讯
     * @param id 资讯ID
     * @param informationDTO 资讯信息DTO
     * @return 资讯VO
     */
    InformationVO updateInformation(Long id, InformationDTO informationDTO);

    /**
     * 修改资讯状态
     * @param id 资讯ID
     * @param status 新状态
     * @return 资讯VO
     */
    Boolean changeStatus(Long id, Integer status);

    /**
     * 分页查询资讯
     * @param pageNumber 页码
     * @param pageSize 每页条数
     * @param title 资讯标题
     * @return 资讯VO分页数据
     */
    IPage<InformationVO> getInformationListByPage(Long pageNumber, Long pageSize, String title);

    /**
     * 获取资讯列表
     * @return 资讯简要信息列表
     */
    List<InformationVO> getInformationList();
    
    /**
     * 根据配置获取资讯列表
     * @param rangeType 范围类型(today, range, count)
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param count 数量
     * @return 资讯简要信息列表
     */
    List<InformationVO> getInformationListByConfig(String rangeType, String startDate, String endDate, Integer count);

    /**
     * 根据ID获取资讯详情
     * @param id 资讯ID
     * @return 资讯VO
     */
    InformationVO getInformationDetail(String id);
}