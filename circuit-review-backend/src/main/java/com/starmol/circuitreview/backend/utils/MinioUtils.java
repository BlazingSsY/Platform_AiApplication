package com.starmol.circuitreview.backend.utils;

import com.starmol.circuitreview.backend.exception.FileException;
import com.starmol.circuitreview.backend.exception.KnowException;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import io.minio.BucketExistsArgs;
import io.minio.DeleteObjectTagsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetObjectTagsArgs;
import lombok.extern.slf4j.Slf4j;

import static com.starmol.circuitreview.backend.utils.FileUtils.getFileExtension;

/**
 * MinIO文件操作工具类
 * 提供常用的MinIO文件上传、下载、删除等操作
 */
@Slf4j
public class MinioUtils {

    /**
     * 检查存储桶是否存在，如果不存在则创建
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     */
    public static void ensureBucketExists(MinioClient minioClient, String bucketName) {
        try {
            boolean bucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created successfully", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to check or create bucket: {}", bucketName, e);
            throw new FileException("创建存储桶失败: " + e.getMessage());
        }
    }

    /**
     * 上传文件到MinIO
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     * @param file        要上传的文件
     * @return 文件ID（SHA-256哈希值）
     */
    public static String uploadFile(MinioClient minioClient, String bucketName, MultipartFile file, String originalFilename) {
        if (file == null || file.isEmpty()) {
            throw new KnowException("文件不能为空");
        }

        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new KnowException("文件名不能为空");
        }

        try {
            //final byte[] fileBytes = file.getBytes();
            //final String fileId = generateFileId(fileBytes);
            final String fileId = UUIDUtils.generateRandomUUID().toLowerCase(); //生成随机文件名

            // 提取文件后缀
            String fileExtension = getFileExtension(originalFilename);
            String objectName = fileId + fileExtension;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );

            log.info("File uploaded successfully to MinIO with ID (SHA-256): {}", objectName);
            return objectName;
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO: {}", originalFilename, e);
            throw new FileException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传本地文件到MinIO
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     * @param filePath    本地文件路径
     * @return 文件ID（SHA-256哈希值）
     */
    public static String uploadFile(MinioClient minioClient, String bucketName, String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new FileException("文件路径不能为空");
        }

        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileException("文件不存在: " + filePath);
        }

        if (!Files.isRegularFile(path)) {
            throw new FileException("指定路径不是文件: " + filePath);
        }

        try {
            // 读取文件内容生成文件ID
            final byte[] fileBytes = Files.readAllBytes(path);
            final String fileId = generateFileId(fileBytes);

            // 获取文件大小
            final long fileSize = Files.size(path);

            // 尝试推断文件类型
            String contentType = Files.probeContentType(path);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 提取文件后缀
            String fileExtension = getFileExtension(path.getFileName().toString());
            String objectName = fileId + fileExtension;

            // 上传文件
            try (InputStream inputStream = new FileInputStream(path.toFile())) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .contentType(contentType)
                                .stream(inputStream, fileSize, -1)
                                .build()
                );
            }

            log.info("File uploaded successfully to MinIO from path '{}' with ID (SHA-256): {}", filePath, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("Failed to upload file from path '{}' to MinIO: {}", filePath, e.getMessage(), e);
            throw new FileException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传输入流到MinIO
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     * @param fileId      文件ID
     * @param inputStream 输入流
     * @param size        文件大小
     * @param contentType 内容类型
     */
    public static void uploadStream(MinioClient minioClient, String bucketName, String fileId, 
                                   InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileId)
                            .contentType(contentType)
                            .stream(inputStream, size, -1)
                            .build()
            );
            log.info("Stream uploaded successfully to MinIO with ID: {}", fileId);
        } catch (Exception e) {
            log.error("Failed to upload stream to MinIO: {}", fileId, e);
            throw new FileException("流上传失败: " + e.getMessage());
        }
    }

    /**
     * 从MinIO获取文件对象
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     * @param fileId      文件ID
     * @return GetObjectResponse对象
     */
    public static GetObjectResponse getObject(MinioClient minioClient, String bucketName, String fileId) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileId)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to get object from MinIO: {}", fileId, e);
            throw new FileException("获取文件失败: " + e.getMessage());
        }
    }

    /**
     * 从MinIO下载文件到指定路径
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     * @param fileId      文件ID
     * @param targetPath  目标文件路径
     * @param fileName    文件名
     */
    public static void downloadFileTo(MinioClient minioClient, String bucketName, String fileId, 
                                     Path targetPath, String fileName) {
        if (fileId == null || fileName == null) {
            throw new FileException("文件ID和文件名不能为空");
        }

        try {
            // 确保目标目录存在
            if (!targetPath.toFile().exists()) {
                targetPath.toFile().mkdirs();
            }

            // 构建目标文件路径
            final Path targetFilePath = targetPath.resolve(fileName);

            // 从MinIO获取文件并写入到目标路径
            try (final GetObjectResponse objectResponse = getObject(minioClient, bucketName, fileId)) {
                Files.copy(objectResponse, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                log.info("File downloaded successfully from MinIO to path: {} with name: {}", 
                        targetFilePath, fileName);
            }
        } catch (IOException e) {
            log.error("Failed to download file from MinIO: fileId={}, fileName={}, targetPath={}", 
                    fileId, fileName, targetPath, e);
            throw new FileException("下载文件失败: " + e.getMessage());
        }
    }

    /**
     * 从MinIO删除文件
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     * @param fileId      文件ID
     */
    public static void deleteFile(MinioClient minioClient, String bucketName, String fileId) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileId)
                            .build()
            );
            log.info("File deleted successfully from MinIO: {}", fileId);
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO: {}", fileId, e);
            throw new FileException("删除文件失败: " + e.getMessage());
        }
    }

    /**
     * 生成文件ID（使用SHA-256哈希）
     *
     * @param fileBytes 文件字节数组
     * @return 文件ID
     */
    public static String generateFileId(byte[] fileBytes) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hashBytes = digest.digest(fileBytes);
            return DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new FileException("生成文件ID失败: " + e.getMessage());
        }
    }

    /**
     * 生成文件ID（使用输入流）
     *
     * @param inputStream 输入流
     * @return 文件ID
     */
    public static String generateFileId(InputStream inputStream) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            final byte[] hashBytes = digest.digest();
            return DatatypeConverter.printHexBinary(hashBytes).toLowerCase();
        } catch (Exception e) {
            log.error("Failed to generate file ID from stream", e);
            throw new FileException("生成文件ID失败: " + e.getMessage());
        }
    }


    /**
     * 上传临时文件到MinIO
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     * @param file        要上传的文件
     * @return 文件ID（SHA-256哈希值）
     */
    public static String uploadTempFile(MinioClient minioClient, String bucketName, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileException("文件不能为空");
        }

        final String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new FileException("文件名不能为空");
        }

        try {
            final byte[] fileBytes = file.getBytes();
            final String fileId = generateFileId(fileBytes);

            // 提取文件后缀
            String fileExtension = getFileExtension(originalFilename);
            String objectName = fileId + fileExtension;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );

            Map<String, String> tags = new HashMap<>();
            tags.put("tmp", "1");

            minioClient.setObjectTags(
                    SetObjectTagsArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .tags(tags)
                            .build()
            );

            log.info("File uploaded temp file successfully to MinIO with ID (SHA-256): {}", objectName);
            return objectName;
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO: {}", originalFilename, e);
            throw new FileException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除对象的所有标签，将临时文件转换为永久文件
     *
     * @param minioClient MinIO客户端
     * @param bucketName  存储桶名称
     * @param fileId      文件ID
     */
    public static void removeObjectTags(MinioClient minioClient, String bucketName, String fileId) {
        if (fileId == null || fileId.trim().isEmpty()) {
            throw new FileException("文件ID不能为空");
        }

        try {
            // 确保bucket存在
            ensureBucketExists(minioClient, bucketName);

            // 删除对象的所有标签
            minioClient.deleteObjectTags(
                    DeleteObjectTagsArgs.builder()
                            .bucket(bucketName)
                            .object(fileId)
                            .build()
            );

            log.info("Object tags removed successfully for file ID: {}, converted to permanent file", fileId);
        } catch (Exception e) {
            log.error("Failed to remove object tags for file ID: {}", fileId, e);
            throw new FileException("删除文件标签失败: " + e.getMessage());
        }
    }
} 