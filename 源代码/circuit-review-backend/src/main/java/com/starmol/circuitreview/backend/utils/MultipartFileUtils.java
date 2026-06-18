package com.starmol.circuitreview.backend.utils;

import com.starmol.circuitreview.backend.bean.dto.CustomMultipartFile;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionVO;
import com.starmol.circuitreview.backend.bean.vo.FileVO;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.service.common.StorageService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultipartFileUtils {

    /**
     * 从存储服务创建MultipartFile对象
     * @param storageService 存储服务
     * @param circuitFileVersionVO 当前文件版本信息
     * @return
     * @throws IOException
     */
    public static MultipartFile createMultipartFileFromStorage(StorageService storageService, CircuitFileVersionVO circuitFileVersionVO) throws IOException {
        // 创建FileVO对象用于获取文件流
        FileVO fileVO = new FileVO().setFileId(circuitFileVersionVO.getMinioId()).setFileName(circuitFileVersionVO.getFileName());

        // 从存储服务获取文件输入流
        try (InputStream inputStream = storageService.downloadFileToStream(fileVO)) {
            if (inputStream == null) {
                throw new KnowException("无法获取文件内容");
            }

            // 读取文件内容
            byte[] fileContent = inputStream.readAllBytes();

            // 创建自定义MultipartFile对象
            return new CustomMultipartFile(
                    "file",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + circuitFileVersionVO.getFileName(),
                    "application/octet-stream",
                    fileContent
            );
        }
        catch (Exception e) {
            throw new KnowException("无法获取文件内容，请联系管理员确认文件可访问状态");
        }
    }
}
