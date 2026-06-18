package com.starmol.logicreview.service.common;

import com.starmol.logicreview.bean.vo.FileVO;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface StorageService {
    String uploadFile(MultipartFile file);
    void downloadFile(String fileId, String fileName, HttpServletResponse response) throws IOException;

    @SneakyThrows
    void downloadFileTo(FileVO fileInfo, Path path);

    void deleteFile(String fileId);

    /**
     * 上传临时文件，会自动设置tmp=1标签以触发生命周期管理
     *
     * @param file 要上传的文件
     * @return 文件ID
     */
    String uploadTempFile(MultipartFile file);

    /**
     * 删除文件的所有标签，将临时文件转换为永久文件
     *
     * @param fileId 文件ID
     */
    void removeFileTags(String fileId);

    InputStream downloadFileToStream(FileVO appendFile);
}