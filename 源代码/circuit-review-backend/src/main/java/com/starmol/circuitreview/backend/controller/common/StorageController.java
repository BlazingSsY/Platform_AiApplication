package com.starmol.circuitreview.backend.controller.common;

import com.starmol.circuitreview.backend.aop.Permit;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.service.common.StorageService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "文件上传下载管理")
@RestController
@RequestMapping("/common/v1/storage")
public class StorageController {
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Permit("all")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseWrapper<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileId = storageService.uploadFile(file, file.getOriginalFilename());
        return ResponseWrapper.success(fileId);
    }

    @Permit("all")
    @GetMapping("/download/{fileId}")
    public void downloadFile(@PathVariable String fileId, @RequestParam(required = false) String fileName, HttpServletResponse response) throws IOException {
        storageService.downloadFile(fileId, fileName, response);
    }
}