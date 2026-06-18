package com.starmol.logicreview.utils;

import com.starmol.logicreview.bean.dto.SourceCodeReviewStatisticsExportDataDTO;
import com.starmol.logicreview.bean.vo.SourceCodeFileDetailVO;
import com.starmol.logicreview.bean.vo.StatisticsExportRequestVO;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.AxisCrosses;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.AxisTickLabelPosition;
import org.apache.poi.xddf.usermodel.chart.AxisTickMark;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFLineChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.SneakyThrows;

@Component
public class ExcelExportUtils {

    public static byte[] exportStatisticsToExcel(List<SourceCodeReviewStatisticsExportDataDTO> data, StatisticsExportRequestVO requestVO) throws IOException {
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
        titleCell.setCellValue("各单位审查代码汇总数据");
        titleCell.setCellStyle(createTitleStyle(workbook));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5)); // 合并单元格

        // 创建表头
        Row headerRow = sheet.createRow(2); // 留出空行作为标题和表头之间的间距
        String[] headers = {"序号", "单位", "代码文件数量", "问题已关闭代码文件数量", "检查文件数量", "通过检查文件数量"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 按代码文件数从大到小排序
        List<SourceCodeReviewStatisticsExportDataDTO> sortedData = data.stream()
                .sorted((a, b) -> {
                    Integer countA = a.getSourceFileCount() != null ? a.getSourceFileCount() : 0;
                    Integer countB = b.getSourceFileCount() != null ? b.getSourceFileCount() : 0;
                    return countB.compareTo(countA); // 降序排序
                })
                .toList();

        // 填充数据
        for (int i = 0; i < sortedData.size(); i++) {
            SourceCodeReviewStatisticsExportDataDTO dto = sortedData.get(i);
            Row dataRow = sheet.createRow(i + 3); // 从第4行开始填充数据

            dataRow.createCell(0).setCellValue(i + 1); // 序号
            dataRow.createCell(1).setCellValue(dto.getDepartmentName() != null ? dto.getDepartmentName() : ""); // 单位
            dataRow.createCell(2).setCellValue(dto.getSourceFileCount() != null ? dto.getSourceFileCount() : 0); // 代码文件数量
            dataRow.createCell(3).setCellValue(dto.getClosedLoopCount() != null ? dto.getClosedLoopCount() : 0); // 问题已关闭代码文件数量
            dataRow.createCell(4).setCellValue(dto.getFileCount() != null ? dto.getFileCount() : 0); // 检查文件数量
            dataRow.createCell(5).setCellValue(dto.getPassFileCount() != null ? dto.getPassFileCount() : 0); // 问题点数
        }
        // 创建图表
        createChart(workbook, sheet, sortedData);
        // 设置最小列宽可容纳15个字符
        for (int i = 0; i < headers.length; i++) {
            sheet.setColumnWidth(i, 256 * 15);
        }
        sheet.setColumnWidth(headers.length - 1, 256 * 20);// 设置最小列宽可容纳20个字符

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

    private static void createChart(XSSFWorkbook workbook, XSSFSheet sheet, List<SourceCodeReviewStatisticsExportDataDTO> data) {
        // 如果数据为空，不创建图表
        if (data == null || data.isEmpty()) {
            return;
        }
        // 数据已经按fileCounts排序，直接使用表格中的数据作为图表数据源
        // 表格数据从第4行开始（索引3），单位在列索引1，fileCounts在列索引2，closedLoopFileCounts在列索引3

        // 创建绘图区域
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        // 定义图表位置和大小 - 从表格下方开始放置图表
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, data.size() + 5, 15, data.size() + 20);

        // 创建图表
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("各单位审查代码统计图");
        chart.setTitleOverlay(false);

        // 创建图表数据
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);

        // 定义数据源 - 直接使用表格中的数据
        // 数据从第4行开始（索引3），到第3+data.size()-1行结束
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(3, 3 + data.size() - 1, 1, 1)); // 单位列在索引1
        XDDFNumericalDataSource<Double> values1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(3, 3 + data.size() - 1, 2, 2)); // 代码文件数量列在索引2
        XDDFNumericalDataSource<Double> values2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(3, 3 + data.size() - 1, 3, 3)); // 问题已关闭代码文件数量列在索引3

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
        barSeries.setTitle("审查代码文件数量", null);
        chart.plot(barChartData);

        // 创建折线图
        XDDFLineChartData lineChartData = (XDDFLineChartData) chart.createData(ChartTypes.LINE, categoryAxis, valueAxis);
        XDDFLineChartData.Series lineSeries = (XDDFLineChartData.Series) lineChartData.addSeries(categories, values2);
        lineSeries.setTitle("问题已关闭代码文件数量", null);
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
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
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
    public static byte[] exportStatisticsTableToExcel(List<SourceCodeFileDetailVO> result) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        // 构建sheet名称
        XSSFSheet sheet = workbook.createSheet("统计分析表");

        // 创建样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // 创建表头
        Row headerRow = sheet.createRow(0); // 留出空行作为标题和表头之间的间距
        //String[] headers = {"序号", "单位", "用户名", "网表文件", "审查点数", "问题点数", "关闭审查点数", "审查时间", "通过率", "状态", "配套机型", "产品型号", "产品名称", "电路原理图号", "版本"};
        String[] headers = {"序号", "单位",  "用户名",  "代码文件", "配套机型", "产品型号", "产品名称", "配置项名称", "版本", "审查文件数", "通过审查文件数", "问题数量", "审查时间", "通过率"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        for (int i = 0; i < result.size(); i++) {
            SourceCodeFileDetailVO sourceCodeFileDetailVO = result.get(i);
            Row dataRow = sheet.createRow(i + 1); // 从第2行开始填充数据

            Cell cell0 = dataRow.createCell(0);
            cell0.setCellValue(i + 1); // 序号
            cell0.setCellStyle(dataStyle);
            Cell cell1 = dataRow.createCell(1);
            cell1.setCellValue(sourceCodeFileDetailVO.getDepartmentName() != null ? sourceCodeFileDetailVO.getDepartmentName() : ""); // 单位
            cell1.setCellStyle(dataStyle);
            if (sourceCodeFileDetailVO.getOwnerName() != null) {
                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue(sourceCodeFileDetailVO.getOwnerName());
                cell2.setCellStyle(dataStyle);
            } else {
                Cell cell2 = dataRow.createCell(2);
                cell2.setCellValue("-");
                cell2.setCellStyle(dataStyle);
            } // 用户名
            if (sourceCodeFileDetailVO.getFileName() != null) {
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue(sourceCodeFileDetailVO.getFileName());
                cell3.setCellStyle(dataStyle);
            } else {
                Cell cell3 = dataRow.createCell(3);
                cell3.setCellValue("-");
                cell3.setCellStyle(dataStyle);
            } // 代码文件
            if (sourceCodeFileDetailVO.getCompatibleModels() != null) {
                Cell cell6 = dataRow.createCell(4);
                cell6.setCellValue(sourceCodeFileDetailVO.getCompatibleModels());
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(4);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } // 配套机型
            if (sourceCodeFileDetailVO.getProductModel() != null) {
                Cell cell6 = dataRow.createCell(5);
                cell6.setCellValue(sourceCodeFileDetailVO.getProductModel());
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(5);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } // 产品型号
            if (sourceCodeFileDetailVO.getProductName() != null) {
                Cell cell6 = dataRow.createCell(6);
                cell6.setCellValue(sourceCodeFileDetailVO.getProductName());
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(6);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } // 产品名称
            if (sourceCodeFileDetailVO.getConfigName() != null) {
                Cell cell6 = dataRow.createCell(7);
                cell6.setCellValue(sourceCodeFileDetailVO.getConfigName());
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(7);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } // 配置名称
            if (sourceCodeFileDetailVO.getCodeFileVersion() != null) {
                Cell cell6 = dataRow.createCell(8);
                cell6.setCellValue(sourceCodeFileDetailVO.getCodeFileVersion());
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(8);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } // 版本
            if (sourceCodeFileDetailVO.getCheckPoints() != null) {
                Cell cell4 = dataRow.createCell(9);
                cell4.setCellValue(sourceCodeFileDetailVO.getCheckPoints());
                cell4.setCellStyle(dataStyle);
            } else {
                Cell cell4 = dataRow.createCell(9);
                cell4.setCellValue("-");
                cell4.setCellStyle(dataStyle);
            } // 审查文件数
            if (sourceCodeFileDetailVO.getPassCheckPoints() != null) {
                Cell cell5 = dataRow.createCell(10);
                cell5.setCellValue(sourceCodeFileDetailVO.getPassCheckPoints());
                cell5.setCellStyle(dataStyle);
            } else {
                Cell cell5 = dataRow.createCell(10);
                cell5.setCellValue("-");
                cell5.setCellStyle(dataStyle);
            } // 通过文件数
            if (sourceCodeFileDetailVO.getQuestions() != null) {
                Cell cell6 = dataRow.createCell(11);
                cell6.setCellValue(sourceCodeFileDetailVO.getQuestions());
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(11);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } //问题数量
            if (sourceCodeFileDetailVO.getReviewTime() != null) {
                Cell cell6 = dataRow.createCell(12);
                cell6.setCellValue(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(sourceCodeFileDetailVO.getReviewTime()));
                cell6.setCellStyle(dataStyle);
            } else {
                Cell cell6 = dataRow.createCell(12);
                cell6.setCellValue("-");
                cell6.setCellStyle(dataStyle);
            } // 审查时间
            if (sourceCodeFileDetailVO.getPassRate() != null) {
                Cell cell7 = dataRow.createCell(13);
                DecimalFormat df = new DecimalFormat("#0.00%");
                cell7.setCellValue(df.format(sourceCodeFileDetailVO.getPassRate()));
                cell7.setCellStyle(dataStyle);
            } else {
                Cell cell7 = dataRow.createCell(13);
                cell7.setCellValue("-");
                cell7.setCellStyle(dataStyle);
            } // 通过率
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