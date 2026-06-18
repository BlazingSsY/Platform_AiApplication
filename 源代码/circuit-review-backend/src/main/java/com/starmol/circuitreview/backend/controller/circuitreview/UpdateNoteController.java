package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.UpdateNoteDTO;
import com.starmol.circuitreview.backend.bean.dto.UpdateNoteUpdateDTO;
import com.starmol.circuitreview.backend.bean.vo.UpdateNoteVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.model.circuitreview.UpdateNote;
import com.starmol.circuitreview.backend.service.circuitreview.UpdateNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/update-note")
@Tag(name = "更新说明管理", description = "更新说明相关接口")
public class UpdateNoteController {
    private final UpdateNoteService updateNoteService;

    @PostMapping
    @Operation(summary = "创建更新说明", description = "创建新的更新说明")
    public ResponseWrapper<UpdateNoteVO> createUpdateNote(@Valid @RequestBody UpdateNoteDTO updateNoteDTO) {
        try {
            UpdateNote updateNote = new UpdateNote();
            updateNote.setUpdateTime(updateNoteDTO.getUpdateTime());
            updateNote.setContent(updateNoteDTO.getContent());

            UpdateNoteVO result = updateNoteService.createUpdateNote(updateNote);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建更新说明失败", e);
            return ResponseWrapper.fail("创建更新说明失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新更新说明", description = "根据ID更新更新说明")
    public ResponseWrapper<UpdateNoteVO> updateUpdateNote(
            @Parameter(description = "更新说明ID") @PathVariable Long id,
            @Valid @RequestBody UpdateNoteUpdateDTO updateNoteUpdateDTO) {
        try {
            UpdateNoteVO result = updateNoteService.updateUpdateNote(id, updateNoteUpdateDTO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新更新说明失败", e);
            return ResponseWrapper.fail("更新更新说明失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取更新说明", description = "根据ID获取更新说明详情")
    public ResponseWrapper<UpdateNoteVO> getUpdateNote(
            @Parameter(description = "更新说明ID") @PathVariable Long id) {
        try {
            UpdateNoteVO result = updateNoteService.getUpdateNoteVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("更新说明不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取更新说明失败", e);
            return ResponseWrapper.fail("获取更新说明失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除更新说明", description = "根据ID删除更新说明")
    public ResponseWrapper<String> deleteUpdateNote(
            @Parameter(description = "更新说明ID") @PathVariable Long id) {
        try {
            boolean result = updateNoteService.removeById(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除更新说明失败", e);
            return ResponseWrapper.fail("删除更新说明失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询更新说明", description = "分页查询更新说明列表")
    public ResponseWrapper<IPage<UpdateNoteVO>> getUpdateNotePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize) {
        try {
            Page<UpdateNote> page = new Page<>(pageNumber, pageSize);
            IPage<UpdateNoteVO> result = updateNoteService.getUpdateNoteVOPage(page);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询更新说明失败", e);
            return ResponseWrapper.fail("分页查询更新说明失败: " + e.getMessage());
        }
    }
}
