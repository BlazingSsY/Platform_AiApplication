package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.UpdateNoteUpdateDTO;
import com.starmol.circuitreview.backend.bean.vo.UpdateNoteVO;
import com.starmol.circuitreview.backend.model.circuitreview.UpdateNote;
import com.starmol.circuitreview.backend.service.base.BaseCascadeService;

import javax.validation.Valid;

public interface UpdateNoteService extends BaseCascadeService<UpdateNote> {
    /**
     * 创建更新说明
     * @param updateNote 更新说明实体
     * @return 更新说明VO
     */
    UpdateNoteVO createUpdateNote(UpdateNote updateNote);

    /**
     * 分页查询更新说明
     * @param page 分页参数
     * @return 更新说明分页结果
     */
    IPage<UpdateNoteVO> getUpdateNoteVOPage(Page<UpdateNote> page);

    /**
     * 根据ID更新更新说明
     * @param id 更新说明ID
     * @param updateNoteUpdateDTO 更新说明实体
     * @return 更新说明VO
     */
    UpdateNoteVO updateUpdateNote(Long id, @Valid UpdateNoteUpdateDTO updateNoteUpdateDTO);

    /**
     * 根据ID获取更新说明
     * @param id 更新说明ID
     * @return 更新说明VO
     */
    UpdateNoteVO getUpdateNoteVOById(Long id);
}
