package com.starmol.logicreview.utils;

import com.alibaba.excel.EasyExcel;

import org.apache.commons.collections.CollectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class ExportUtils {

    public static <T> void exportDataWithClass(String fName, Class<T> clazz, List<T> data) throws IOException {
        exportData(fName, clazz, null, data);
    }

    public static <T> void exportDataWithCustomerHeader(String fName, List<List<String>> header, List<T> data) throws IOException {
        exportData(fName, null, header, data);
    }

    public static <T> void exportData(String fName, Class<T> clazz, List<List<String>> header, List<T> data) throws IOException {
        final HttpServletResponse response = HttpRequestUtil.getResponse();
        final String time = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        final String fileName = URLEncoder.encode(fName + "_" + time, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        if (CollectionUtils.isEmpty(header)) {
            EasyExcel.write(response.getOutputStream(), clazz).sheet("导出列表").doWrite(data);
            return;
        }

        EasyExcel.write(response.getOutputStream()).head(header).sheet("导出列表").doWrite(data);
    }
}
