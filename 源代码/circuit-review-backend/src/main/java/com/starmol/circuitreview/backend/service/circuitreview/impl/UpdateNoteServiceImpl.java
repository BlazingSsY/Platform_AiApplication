package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.UpdateNoteUpdateDTO;
import com.starmol.circuitreview.backend.bean.vo.UpdateNoteVO;
import com.starmol.circuitreview.backend.model.circuitreview.UpdateNote;
import com.starmol.circuitreview.backend.repository.circuitreview.UpdateNoteMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.UpdateNoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateNoteServiceImpl extends BaseCascadeServiceImpl<UpdateNoteMapper, UpdateNote> implements UpdateNoteService {

    private final ObjectMapper objectMapper;

    @Override
    public UpdateNoteVO createUpdateNote(UpdateNote updateNote) {
        UpdateNote savedUpdateNote = this.saveAndReturnObject(updateNote);
        return objectMapper.convertValue(savedUpdateNote, UpdateNoteVO.class);
    }

    @Override
    public IPage<UpdateNoteVO> getUpdateNoteVOPage(Page<UpdateNote> page) {
        IPage<UpdateNote> updateNotePage = this.page(page, Wrappers.<UpdateNote>lambdaQuery().orderByDesc(UpdateNote::getUpdateTime));
        return updateNotePage.convert(updateNote ->
            objectMapper.convertValue(updateNote, UpdateNoteVO.class));
    }

    @Override
    public UpdateNoteVO updateUpdateNote(Long id, @Valid UpdateNoteUpdateDTO updateNoteUpdateDTO) {
        UpdateNote updateNote = new UpdateNote();
        updateNote.setId(id);
        updateNote.setUpdateTime(updateNoteUpdateDTO.getUpdateTime());
        updateNote.setContent(updateNoteUpdateDTO.getContent());
        UpdateNote updatedUpdateNote = this.updateByIDAndReturnObject(updateNote);
        return objectMapper.convertValue(updatedUpdateNote, UpdateNoteVO.class);
    }

    @Override
    public UpdateNoteVO getUpdateNoteVOById(Long id) {
        UpdateNote updateNote = this.getById(id);
        if (updateNote == null) {
            return null;
        }
        return objectMapper.convertValue(updateNote, UpdateNoteVO.class);
    }
}
