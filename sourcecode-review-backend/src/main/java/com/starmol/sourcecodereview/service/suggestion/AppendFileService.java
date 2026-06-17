package com.starmol.sourcecodereview.service.suggestion;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.sourcecodereview.bean.dto.AppendFileDTO;
import com.starmol.sourcecodereview.bean.vo.AppendFileVO;
import com.starmol.sourcecodereview.model.base.DeleteDTO;
import com.starmol.sourcecodereview.model.suggestion.AppendFile;
import com.starmol.sourcecodereview.service.base.BaseService;

import java.util.List;
import java.util.Map;

/**
 * 附件逻辑层
 *
 * @author Yuexiaopeng
 * @date 2018/12/27 14:36
 */
public interface AppendFileService extends BaseService<AppendFile> {


    /**
     * 创建附件文件
     */
    AppendFileVO createAppendFile(AppendFileDTO appendFileDTO);


    /**
     * 根据主记录ID列表删除附件记录(主记录删除后调用)
     *
     * @param fidList 要删除的主记录ID     */
    void  deleteAppendFilesByFid(List<Long> fidList);


    /**
     * 更新附件文件
     */
    AppendFileVO updateAppendFile(Long id, AppendFileDTO appendFileDTO);

    /**
     * 分页获取附件文件
     */
    IPage<AppendFileVO> getAppendFileVOByPage(IPage<AppendFile> objectPage, String type);

    AppendFileVO getAppendFileVOById(Long id);

    /**
     * 创建附件列表,返回列表不保存
     */
    List<AppendFile> createAppendFilesWithoutSave(List<AppendFileDTO> appendFileDTOList, Long fid, String type);

    /**
     * 创建附件列表,在主记录保存完成后调用
     */
    void createAppendFiles(List<AppendFileDTO> appendFileDTOList, Long fid, String type);
   /**
     * 更新附件表(主表修改保存完成后调用):增加列表中新增的附加,删除列表中不存在的附加
     *
     * @param appendFileDTOList 修改后新的附件列表的
     */
    void  updateAppendFiles(List<AppendFileDTO> appendFileDTOList, Long fid, String type);

    List<AppendFileDTO> findByAppendFId(String fId, String type);

    /**
     * 用事务的方式删除附件列表
     *
     * @param deleteAppendFiles 要删除的附件列表
     */
    void  deleteAppendFilesWithTransaction(List<DeleteDTO> deleteAppendFiles);



    /**
     * 根据附件名查询附件
     *
     * @param appendFileName 附件名(附件名唯一)
     * @return 附件对象
     */
    AppendFile findByAppendFileName(String appendFileName);

    /**
     * 根据父记录ID列表,获取按照类型分组的Map
     *
     * @param fidList 父记录ID列表
     * @return 附件对象
     */
    Map<String, List<AppendFile>> getAppendAllFileMap(List<Long> fidList);

   Map<Long, List<AppendFile>> getAppendFileMap(List<Long> fidList, String type);


}
