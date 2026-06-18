package com.starmol.circuitreview.backend.utils;

import com.starmol.circuitreview.backend.bean.vo.DirectoryVO;

import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * 下载文件工具类
 * @Author Yuexiaopeng
 * @Date 2024/10/16 9:15
 */

@Slf4j
public class DownloadUtil {

    /**
     * 文件下载
     * @param response
     * @param filePath 绝对路径
     */
    public static boolean download(HttpServletResponse response, HttpServletRequest request, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()){
            log.error("文件不存在或文件失效！");
            return false;
        }
        String contentType = request.getServletContext().getMimeType(file.getAbsolutePath());
        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }
        response.reset();
        response.setContentType(contentType);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition","attachment;filename="+file.getName());
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)); OutputStream out = response.getOutputStream()){
            IOUtils.copy(bis, out);
            response.flushBuffer();
        }
        return true;
    }

    /**
     * 文件下载
     * @param response
     */
    public static boolean download(HttpServletResponse response, InputStream inputStream, String filename) throws IOException {
        // 清空response
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-disposition","attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        try(BufferedInputStream bis = new BufferedInputStream(inputStream); OutputStream out = response.getOutputStream()){
            IOUtils.copy(bis, out);
            response.flushBuffer();
        }
        return true;
    }

    public static List<String> getNamesFromPath(String path){
        if (path == null || path.trim().isEmpty()) {
            return Collections.emptyList();
        }

        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            return Collections.emptyList();
        }
        try {
            return Files.list(directory.toPath())
                    .filter(p -> Files.isRegularFile(p) && !p.getFileName().toString().startsWith("."))
                    .map(f -> f.getFileName().toString())
                    .distinct()
                    .collect(Collectors.toList());
        } catch (IOException e) {
            // 可选：记录日志
            return Collections.emptyList();
        }
    }

    public static List<DirectoryVO> getDirectoryFromPath(String root, String current) {
        File rootDir = new File(root);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            return new ArrayList<>();
        }

        final File currentPath = new File(rootDir + current);
        if (!currentPath.exists() || !currentPath.isDirectory()) {
            return new ArrayList<>();
        }

        List<DirectoryVO> result = new ArrayList<>();
        processDirectory(rootDir, currentPath, result);
        return result;
    }

    /**
     * 递归处理目录，将所有文件添加到结果列表中
     *
     * @param rootDir 根目录
     * @param currentFile 当前正在处理的文件或目录
     * @param result 结果列表
     */
    private static void processDirectory(File rootDir, File currentFile, List<DirectoryVO> result) {
        if (currentFile.isFile()) {
            DirectoryVO vo = new DirectoryVO();
            // 计算相对路径并按目录层级拆分
            String relativePath = currentFile.getAbsolutePath().substring(rootDir.getAbsolutePath().length() + 1);
            vo.setName(relativePath);
            
            // 获取上级文件夹名称并设置到directoryName字段
            File parentDir = currentFile.getParentFile();
            if (parentDir != null) {
                vo.setDirectoryName(parentDir.getName());
            }
            
            result.add(vo);
        } else if (currentFile.isDirectory()) {
            File[] files = currentFile.listFiles();
            if (files != null) {
                for (File file : files) {
                    processDirectory(rootDir, file, result);
                }
            }
        }
    }

    /**
     * 提取文件名并去除扩展名
     */
    private static String extractFileNameWithoutExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        return fileName;
        //int dotIndex = fileName.lastIndexOf('.');
        //return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}
