package com.starmol.logicreview.service.common.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.starmol.logicreview.bean.vo.FileVO;
import com.starmol.logicreview.exception.FileException;
import com.starmol.logicreview.service.common.StorageService;
import com.starmol.logicreview.utils.MinioUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@ConditionalOnProperty(name = "storage.minio.enable", havingValue = "true")
@Slf4j
public class MinioStorageServiceImpl implements StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:resource}")
    private String bucketName;

    @Override
    public String uploadFile(final MultipartFile file) {
        // 使用工具类上传文件
        return MinioUtils.uploadFile(minioClient, bucketName, file);
    }

    @Override
    public String uploadTempFile(final MultipartFile file) {
        // 使用工具类上传临时文件并设置标签
        return MinioUtils.uploadTempFile(minioClient, bucketName, file);
    }

    @Override
    public void removeFileTags(final String fileId) {
        // 使用工具类删除文件标签
        MinioUtils.removeObjectTags(minioClient, bucketName, fileId);
    }

    @SneakyThrows
    @Override
    public InputStream downloadFileToStream(FileVO fileInfo) {
        if (fileInfo == null || fileInfo.getFileId() == null || fileInfo.getFileName() == null) {
            throw new FileException("文件信息或文件ID、文件名不能为空");
        }

        // 从MinIO获取文件
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileInfo.getFileId())
                        .build());
    }

    @Override
    public void downloadFile(final String fileId, String fileName, final HttpServletResponse response) throws IOException {
        // 使用工具类获取文件对象
        try (final GetObjectResponse objectResponse = MinioUtils.getObject(minioClient, bucketName, fileId)) {
            // 设置响应头
            response.reset();
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            String downloadFileName = StringUtils.isBlank(fileName) ? fileId : fileName;
            response.setHeader("Content-disposition", "attachment;filename=" +
                    URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8).replace("+", "%20"));

            // 将文件内容复制到响应输出流
            final byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = objectResponse.read(buffer)) != -1) {
                response.getOutputStream().write(buffer, 0, bytesRead);
            }

        response.flushBuffer();
    } catch (Exception e) {
        log.error("Failed to download file: {}", fileId, e);
        throw new IOException("下载文件失败: " + e.getMessage(), e);
    }
    }

    @SneakyThrows
    @Override
    public void downloadFileTo(final FileVO fileInfo, final Path path) {
        if (fileInfo == null || fileInfo.getFileId() == null || fileInfo.getFileName() == null) {
            throw new FileException("文件信息或文件ID、文件名不能为空");
        }

        // 确保目标目录存在
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }

        // 构建目标文件路径
        final Path targetFilePath = path.resolve(fileInfo.getFileName());

        // 从MinIO获取文件
        try (final GetObjectResponse objectResponse = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileInfo.getFileId())
                        .build()
        )) {
            // 将文件内容写入到目标路径，如果文件已存在则覆盖
            Files.copy(objectResponse, targetFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            log.info("File downloaded successfully from MinIO to path: {} with name: {}",
                    targetFilePath, fileInfo.getFileName());
        } catch (Exception e) {
            log.error("Failed to download file from MinIO: fileId={}, fileName={}, targetPath={}",
                    fileInfo.getFileId(), fileInfo.getFileName(), targetFilePath, e);
            throw new IOException("下载文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    @SneakyThrows
    public void deleteFile(final String fileId) {
        // 使用工具类删除文件
        MinioUtils.deleteFile(minioClient, bucketName, fileId);
    }
} 