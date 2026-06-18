package com.starmol.circuitreview.backend.utils;

import com.starmol.circuitreview.backend.bean.dto.CircuitReviewStatisticsExportDataDTO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileDetailVO;
import com.starmol.circuitreview.backend.bean.vo.StatisticsExportRequestVO;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class ExcelExportUtils {

    public static byte[] exportStatisticsToExcel(List<CircuitReviewStatisticsExportDataDTO> data, StatisticsExportRequestVO requestVO) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 构建sheet名称
        String sheetName = buildSheetName(requestVO);
        XSSFSheet sheet = workbook.createSheet(sheetName);

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // 添加表格标题
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("各单位审查电路原理图汇总数据");
        titleCell.setCellStyle(createTitleStyle(workbook));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6)); // 合并单元格

        // 创建表头
        Row headerRow = sheet.createRow(2); // 留出空行作为标题和表头之间的间距
        String[] headers = {"序号", "单位", "审查原理图数", "问题已关闭原理图数", "审查点数", "问题点数", "关闭问题点数", "超半月未关闭原理图数"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 按审查电路原理图数量从大到小排序
        List<CircuitReviewStatisticsExportDataDTO> sortedData = data.stream()
                .sorted((a, b) -> {
                    Integer countA = a.getFileCounts() != null ? a.getFileCounts() : 0;
                    Integer countB = b.getFileCounts() != null ? b.getFileCounts() : 0;
                    return countB.compareTo(countA); // 降序排序
                })
                .toList();

        // 填充数据
        for (int i = 0; i < sortedData.size(); i++) {
            CircuitReviewStatisticsExportDataDTO dto = sortedData.get(i);
            Row dataRow = sheet.createRow(i + 3); // 从第4行开始填充数据

            Cell cell0 = dataRow.createCell(0);
            cell0.setCellValue(i + 1); // 序号
            cell0.setCellStyle(dataStyle);
            Cell cell1 = dataRow.createCell(1);
            cell1.setCellValue(dto.getDepartmentName() != null ? dto.getDepartmentName() : ""); // 单位
            cell1.setCellStyle(dataStyle);
            if (dto.getFileCounts() != null) {
                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue(dto.getFileCounts());
                cell2.setCellStyle(dataStyle);
            } else {
                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue("-");
                cell2.setCellStyle(dataStyle);
            } // 审查电路原理图数量
            if (dto.getClosedLoopFileCounts() != null) {
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue(dto.getClosedLoopFileCounts());
                cell3.setCellStyle(dataStyle);
            } else {
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue("-");
                cell3.setCellStyle(dataStyle);
            } // 问题已关闭电路原理图数量
            if (dto.getTotalCheckPoints() != null) {
                Cell cell4 = dataRow.createCell(4);
                cell4.setCellValue(dto.getTotalCheckPoints());
                cell4.setCellStyle(dataStyle);
            } else {
                Cell cell4 = dataRow.createCell(4);
                cell4.setCellValue("-");
                cell4.setCellStyle(dataStyle);
            } // 审查点数
            if (dto.getTotalFailCheckPoints() != null) {
                Cell cell5 = dataRow.createCell(5);
                cell5.setCellValue(dto.getTotalFailCheckPoints());
                cell5.setCellStyle(dataStyle);
            } else {
                Cell cell5 = dataRow.createCell(5);
                cell5.setCellValue("-");
                cell5.setCellStyle(dataStyle);
            } // 问题点数
            if (dto.getTotalClosedFailCheckPoints() != null) {
                Cell cell6 = dataRow.createCell(6);
                cell6.setCellValue(dto.getTotalClosedFailCheckPoints());
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(6);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } // 关闭问题数量
            if (dto.getExceedHalfMonthNotClosedFiles() != null) {
                Cell cell7 = dataRow.createCell(7);
                cell7.setCellValue(dto.getExceedHalfMonthNotClosedFiles());
                cell7.setCellStyle(dataStyle);
            } else {
                Cell cell7 = dataRow.createCell(7);
                cell7.setCellValue(0);
                cell7.setCellStyle(dataStyle);
            } // 超半月未关闭电路原理图数量
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 创建图表
        createChart(workbook, sheet, sortedData);

        // 将工作簿写入字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    public static String buildSheetName(StatisticsExportRequestVO requestVO) {
        // 获取开始时间和结束时间
        String startDate = null;
        String endDate = null;

        if (requestVO != null) {
            if (requestVO.getStartDate() != null) {
                startDate = requestVO.getStartDate().toString();
            }
            if (requestVO.getEndDate() != null) {
                endDate = requestVO.getEndDate().toString();
            }
        }

        String baseName;
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            baseName = "统计数据_" + startDate + "-" + endDate;
        } else if (startDate != null && !startDate.isEmpty()) {
            baseName = "统计数据_" + startDate + "-至今";
        } else if (endDate != null && !endDate.isEmpty()) {
            baseName = "统计数据_开始-" + endDate;
        } else {
            baseName = "统计数据_全部";
        }

        return baseName;
    }

    public static String buildFileName(StatisticsExportRequestVO requestVO) {
        // 获取开始时间和结束时间
        String startDate = null;
        String endDate = null;

        if (requestVO != null) {
            if (requestVO.getStartDate() != null) {
                startDate = requestVO.getStartDate().toString();
            }
            if (requestVO.getEndDate() != null) {
                endDate = requestVO.getEndDate().toString();
            }
        }

        String baseName;
        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            baseName = "statistics-export_" + startDate + "-" + endDate;
        } else if (startDate != null && !startDate.isEmpty()) {
            baseName = "statistics-export_" + startDate + "-now";
        } else if (endDate != null && !endDate.isEmpty()) {
            baseName = "statistics-export_begin-" + endDate;
        } else {
            baseName = "statistics-export_all";
        }

        return baseName;
    }

    private static void createChart(XSSFWorkbook workbook, XSSFSheet sheet, List<CircuitReviewStatisticsExportDataDTO> data) {
        // 数据已经按fileCounts排序，直接使用表格中的数据作为图表数据源
        // 表格数据从第4行开始（索引3），单位在列索引1，fileCounts在列索引2，closedLoopFileCounts在列索引3

        // 创建绘图区域
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        // 定义图表位置和大小 - 从表格下方开始放置图表
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 1, data.size() + 5, 15, data.size() + 20);

        // 创建图表
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("各单位审查电路原理图统计图");
        chart.setTitleOverlay(false);

        // 创建图表数据
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        // 定义数据源 - 直接使用表格中的数据
        // 数据从第4行开始（索引3），到第3+data.size()-1行结束
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(3, 3 + data.size() - 1, 1, 1)); // 单位列在索引1
        XDDFNumericalDataSource<Double> values1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(3, 3 + data.size() - 1, 2, 2)); // fileCounts列在索引2
        XDDFNumericalDataSource<Double> values2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(3, 3 + data.size() - 1, 3, 3)); // closedLoopFileCounts列在索引3

        // 创建柱状图
        XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        categoryAxis.setCrosses(AxisCrosses.AUTO_ZERO); // 设置轴交叉位置
        categoryAxis.setMajorTickMark(AxisTickMark.NONE); // 设置主要刻度标记
        categoryAxis.setTickLabelPosition(AxisTickLabelPosition.LOW); // 设置刻度标签位置

        XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT);
        valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);

        XDDFBarChartData barChartData = (XDDFBarChartData) chart.createData(ChartTypes.BAR, categoryAxis, valueAxis);
        barChartData.setBarDirection(BarDirection.COL);
        barChartData.setGapWidth(150);
        XDDFBarChartData.Series barSeries = (XDDFBarChartData.Series) barChartData.addSeries(categories, values1);
        barSeries.setTitle("审查电路原理图数量", null);
        chart.plot(barChartData);

        // 创建折线图
        XDDFLineChartData lineChartData = (XDDFLineChartData) chart.createData(ChartTypes.LINE, categoryAxis, valueAxis);
        XDDFLineChartData.Series lineSeries = (XDDFLineChartData.Series) lineChartData.addSeries(categories, values2);
        lineSeries.setTitle("问题已关闭电路原理图数量", null);
        chart.plot(lineChartData);
    }

    private static CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDataStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private static CellStyle createTitleStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    @SneakyThrows
    public static byte[] exportStatisticsTableToExcel(List<CircuitFileDetailVO> result) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 构建sheet名称
        XSSFSheet sheet = workbook.createSheet("统计分析表");

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // 创建表头
        Row headerRow = sheet.createRow(0); // 留出空行作为标题和表头之间的间距
        String[] headers = {"序号", "单位", "用户名", "网表文件", "配套机型", "产品型号", "产品名称", "电路原理图号", "版本", "审查点数", "问题点数", "关闭问题点数", "审查时间", "通过率", "状态", "超半月未关闭"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        for (int i = 0; i < result.size(); i++) {
            CircuitFileDetailVO circuitFileDetailVO = result.get(i);
            Row dataRow = sheet.createRow(i + 1); // 从第2行开始填充数据

            Cell cell0 = dataRow.createCell(0);
            cell0.setCellValue(i + 1); // 序号
            cell0.setCellStyle(dataStyle);
            Cell cell1 = dataRow.createCell(1);
            cell1.setCellValue(circuitFileDetailVO.getDepartmentName() != null ? circuitFileDetailVO.getDepartmentName() : ""); // 单位
            cell1.setCellStyle(dataStyle);
            if (circuitFileDetailVO.getOwnerName() != null) {
                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue(circuitFileDetailVO.getOwnerName());
                cell2.setCellStyle(dataStyle);
            } else {
                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue("-");
                cell2.setCellStyle(dataStyle);
            } // 用户名
            if (circuitFileDetailVO.getFileName() != null) {
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue(circuitFileDetailVO.getFileName());
                cell3.setCellStyle(dataStyle);
            } else {
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue("-");
                cell3.setCellStyle(dataStyle);
            } // 网表文件
            if (circuitFileDetailVO.getCompatibleModels() != null) {
                Cell cell4 = dataRow.createCell(4);
                cell4.setCellValue(circuitFileDetailVO.getCompatibleModels());
                cell4.setCellStyle(dataStyle);
            } else {
                Cell cell4 = dataRow.createCell(4);
                cell4.setCellValue("-");
                cell4.setCellStyle(dataStyle);
            } // 配套机型
            if (circuitFileDetailVO.getProductModel() != null) {
                Cell cell5 = dataRow.createCell(5);
                cell5.setCellValue(circuitFileDetailVO.getProductModel());
                cell5.setCellStyle(dataStyle);
            } else {
                Cell cell5 = dataRow.createCell(5);
                cell5.setCellValue("-");
                cell5.setCellStyle(dataStyle);
            } // 产品型号
            if (circuitFileDetailVO.getProductName() != null) {
                Cell cell6 = dataRow.createCell(6);
                cell6.setCellValue(circuitFileDetailVO.getProductName());
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(6);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } // 产品名称
            if (circuitFileDetailVO.getDiagramNumber() != null) {
                Cell cell7 = dataRow.createCell(7);
                cell7.setCellValue(circuitFileDetailVO.getDiagramNumber());
                cell7.setCellStyle(dataStyle);
            } else {
                Cell cell7 = dataRow.createCell(7);
                cell7.setCellValue("-");
                cell7.setCellStyle(dataStyle);
            } // 电路原理图号
            if (circuitFileDetailVO.getDiagramVersion() != null) {
                Cell cell8 = dataRow.createCell(8);
                cell8.setCellValue(circuitFileDetailVO.getDiagramVersion());
                cell8.setCellStyle(dataStyle);
            } else {
                Cell cell8 = dataRow.createCell(8);
                cell8.setCellValue("-");
                cell8.setCellStyle(dataStyle);
            } // 版本
            if (circuitFileDetailVO.getCheckPoints() != null) {
                Cell cell9 = dataRow.createCell(9);
                cell9.setCellValue(circuitFileDetailVO.getCheckPoints());
                cell9.setCellStyle(dataStyle);
            } else {
                Cell cell9 = dataRow.createCell(9);
                cell9.setCellValue("-");
                cell9.setCellStyle(dataStyle);
            } // 审查点数
            if (circuitFileDetailVO.getFailCheckPoints() != null) {
                Cell cell10 = dataRow.createCell(10);
                cell10.setCellValue(circuitFileDetailVO.getFailCheckPoints());
                cell10.setCellStyle(dataStyle);
            } else {
                Cell cell10 = dataRow.createCell(10);
                cell10.setCellValue("-");
                cell10.setCellStyle(dataStyle);
            } // 问题点数
            if (circuitFileDetailVO.getClosedFailCheckPoints() != null) {
                Cell cell11 = dataRow.createCell(11);
                cell11.setCellValue(circuitFileDetailVO.getClosedFailCheckPoints());
                cell11.setCellStyle(dataStyle);
            } else {
                Cell cell11 = dataRow.createCell(11);
                cell11.setCellValue("-");
                cell11.setCellStyle(dataStyle);
            } // 关闭问题数量
            if (circuitFileDetailVO.getReviewTime() != null) {
                Cell cell12 = dataRow.createCell(12);
                cell12.setCellValue(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(circuitFileDetailVO.getReviewTime()));
                cell12.setCellStyle(dataStyle);
            } else {
                Cell cell12 = dataRow.createCell(12);
                cell12.setCellValue("-");
                cell12.setCellStyle(dataStyle);
            } // 审查时间
            if (circuitFileDetailVO.getPassRate() != null) {
                Cell cell13 = dataRow.createCell(13);
                DecimalFormat df = new DecimalFormat("#0.00%");
                cell13.setCellValue(df.format(circuitFileDetailVO.getPassRate()));
                cell13.setCellStyle(dataStyle);
            } else {
                Cell cell13 = dataRow.createCell(13);
                cell13.setCellValue("-");
                cell13.setCellStyle(dataStyle);
            } // 通过率
            if (circuitFileDetailVO.getIsClosedLoop() != null) {
                Cell cell14 = dataRow.createCell(14);
                cell14.setCellValue(circuitFileDetailVO.getIsClosedLoop().equals(1) ? "问题关闭" : "问题未关闭");
                cell14.setCellStyle(dataStyle);
            } else {
                Cell cell14 = dataRow.createCell(14);
                cell14.setCellValue("-");
                cell14.setCellStyle(dataStyle);
            } // 状态
            if (circuitFileDetailVO.getExceedHalfMonthNotClosed() != null) {
                Cell cell15 = dataRow.createCell(15);
                cell15.setCellValue(circuitFileDetailVO.getExceedHalfMonthNotClosed().equals(1) ? "是" : "否");
                cell15.setCellStyle(dataStyle);
            } else {
                Cell cell15 = dataRow.createCell(15);
                cell15.setCellValue("-");
                cell15.setCellStyle(dataStyle);
            } // 状态
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 将工作簿写入字节数组
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}