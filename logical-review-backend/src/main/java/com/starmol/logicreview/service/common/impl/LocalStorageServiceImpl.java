package com.starmol.logicreview.service.common.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.starmol.logicreview.bean.vo.FileVO;
import com.starmol.logicreview.exception.FileException;
import com.starmol.logicreview.service.common.StorageService;

import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.servlet.http.HttpServletResponse;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(name = "storage.local.enable", havingValue = "true")
@Slf4j
public class LocalStorageServiceImpl implements StorageService {

    @Value("${storage.local.path}")
    private String storagePath;

    @SneakyThrows
    @Override
    public String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new FileException("文件名不能为空");
        }
        final Path path = Paths.get(storagePath + originalFilename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return originalFilename;
    }

    @Override
    public String uploadTempFile(MultipartFile file) {
        // 本地存储不支持标签，直接调用普通上传方法
        log.warn("本地存储不支持标签功能，临时文件将作为普通文件上传");
        return uploadFile(file);
    }

    @Override
    public void removeFileTags(String fileId) {
        // 本地存储不支持标签，记录警告日志
        log.warn("本地存储不支持标签功能，无法删除文件标签，文件ID: {}", fileId);
    }

    @Override
    public InputStream downloadFileToStream(FileVO appendFile) {
        return null;
    }

    @Override
    public void downloadFile(String fileId, String fileName, HttpServletResponse response) {
        // 构建完整的文件路径
        Path filePath = Paths.get(storagePath, fileId.split(",")).normalize();
        File file = filePath.toFile();

        // 检查文件是否存在
        if (!file.exists() || file.isDirectory()) {
            log.error("文件不存在或路径指向的是目录！");
            throw new FileException("文件不存在");
        }

        // 获取文件名
        String downloadFileName = StringUtils.isBlank(fileName) ? file.getName() : fileName;
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8));

        // 使用try-with-resources确保资源正确关闭
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             OutputStream out = response.getOutputStream()) {
            IOUtils.copy(bis, out);
            response.flushBuffer();
        } catch (IOException e) {
            log.error("文件下载失败：" + e.getMessage());
            throw new FileException("文件下载失败");
        }
    }

    @Override
    public void downloadFileTo(FileVO fileInfo, Path path) {

    }

    @Override
    public void deleteFile(String fileId) {
        try {
            Path path = Paths.get(storagePath + fileId);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
}