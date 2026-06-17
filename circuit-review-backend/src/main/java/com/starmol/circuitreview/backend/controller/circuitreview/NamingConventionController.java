package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.NamingConventionDTO;
import com.starmol.circuitreview.backend.bean.dto.NamingConventionUpdateDTO;
import com.starmol.circuitreview.backend.bean.vo.NamingConventionVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.circuitreview.NamingConvention;
import com.starmol.circuitreview.backend.service.circuitreview.NamingConventionService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
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
@RequestMapping("/v1/naming-convention")
@Tag(name = "设计命名规范管理", description = "设计命名规范相关接口")
public class NamingConventionController {
    private final NamingConventionService namingConventionService;
    
    @PostMapping
    @Operation(summary = "创建命名规范", description = "创建新的命名规范")
    public ResponseWrapper<NamingConventionVO> createNamingConvention(@Valid @RequestBody NamingConventionDTO namingConventionDTO) {
        try {
            NamingConvention namingConvention = new NamingConvention();
            namingConvention.setTitle(namingConventionDTO.getTitle());
            namingConvention.setContent(namingConventionDTO.getContent());
            
            NamingConventionVO result = namingConventionService.createNamingConvention(namingConvention);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建命名规范失败", e);
            return ResponseWrapper.fail("创建命名规范失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新命名规范", description = "根据ID更新命名规范")
    public ResponseWrapper<NamingConventionVO> updateNamingConvention(
            @Parameter(description = "命名规范ID") @PathVariable Long id,
            @Valid @RequestBody NamingConventionUpdateDTO namingConventionUpdateDTO) {
        try {
            NamingConventionVO result = namingConventionService.updateNamingConvention(id, namingConventionUpdateDTO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新命名规范失败", e);
            return ResponseWrapper.fail("更新命名规范失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取命名规范", description = "根据ID获取命名规范详情")
    public ResponseWrapper<NamingConventionVO> getNamingConvention(
            @Parameter(description = "命名规范ID") @PathVariable Long id) {
        try {
            NamingConventionVO result = namingConventionService.getNamingConventionVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("命名规范不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取命名规范失败", e);
            return ResponseWrapper.fail("获取命名规范失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除命名规范", description = "根据ID删除命名规范")
    public ResponseWrapper<String> deleteNamingConvention(
            @Parameter(description = "命名规范ID") @PathVariable Long id) {
        try {
            boolean result = namingConventionService.removeById(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除命名规范失败", e);
            return ResponseWrapper.fail("删除命名规范失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/page")
    @Operation(summary = "分页查询命名规范", description = "分页查询命名规范列表")
    public ResponseWrapper<IPage<NamingConventionVO>> getNamingConventionPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize) {
        try {
            Page<NamingConvention> page = new Page<>(pageNumber, pageSize);
            IPage<NamingConventionVO> result = namingConventionService.getNamingConventionVOPage(page);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询命名规范失败", e);
            return ResponseWrapper.fail("分页查询命名规范失败: " + e.getMessage());
        }
    }
}