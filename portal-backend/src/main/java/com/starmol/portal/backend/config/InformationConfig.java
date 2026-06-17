package com.starmol.portal.backend.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Data
@Component
@Slf4j
public class InformationConfig {
    
    // 刷新时间间隔（秒），默认10秒，范围3-10
    private Integer refreshInterval = 10;
    
    // 资讯范围类型：today-当天，range-日期范围，count-条数
    private String rangeType = "today";
    
    // 开始日期（range模式下使用）
    private String startDate;
    
    // 结束日期（range模式下使用）
    private String endDate;
    
    // 获取条数（count模式下使用）
    private Integer count = 10;
    
    // 配置文件路径
    private final String configFilePath = "information-config.properties";
    
    @PostConstruct
    public void loadConfigFromFile() {
        try {
            if (Files.exists(Paths.get(configFilePath))) {
                loadFromFile();
            } else {
                saveToFile();
            }
        } catch (Exception e) {
            log.error("加载资讯配置文件失败", e);
        }
    }
    
    public void saveConfig() {
        try {
            saveToFile();
        } catch (Exception e) {
            log.error("保存资讯配置文件失败", e);
        }
    }
    
    private void loadFromFile() throws IOException {
        try (InputStream input = new FileInputStream(configFilePath)) {
            java.util.Properties prop = new java.util.Properties();
            prop.load(input);
            
            // 加载刷新时间间隔
            String refreshIntervalStr = prop.getProperty("refresh.interval");
            if (refreshIntervalStr != null && !refreshIntervalStr.isEmpty()) {
                try {
                    this.refreshInterval = Integer.parseInt(refreshIntervalStr);
                    // 确保在有效范围内
                    if (this.refreshInterval < 3) this.refreshInterval = 3;
                    if (this.refreshInterval > 10) this.refreshInterval = 10;
                } catch (NumberFormatException e) {
                    this.refreshInterval = 10; // 默认值
                }
            }
            
            // 加载范围类型
            String rangeTypeStr = prop.getProperty("range.type");
            if (rangeTypeStr != null && !rangeTypeStr.isEmpty()) {
                this.rangeType = rangeTypeStr;
            }
            
            // 加载开始日期
            String startDateStr = prop.getProperty("range.start.date");
            if (startDateStr != null) {
                this.startDate = startDateStr;
            }
            
            // 加载结束日期
            String endDateStr = prop.getProperty("range.end.date");
            if (endDateStr != null) {
                this.endDate = endDateStr;
            }
            
            // 加载条数
            String countStr = prop.getProperty("range.count");
            if (countStr != null && !countStr.isEmpty()) {
                try {
                    this.count = Integer.parseInt(countStr);
                } catch (NumberFormatException e) {
                    this.count = 10; // 默认值
                }
            }
        }
    }
    
    private void saveToFile() throws IOException {
        java.util.Properties prop = new java.util.Properties();
        
        prop.setProperty("refresh.interval", String.valueOf(this.refreshInterval));
        prop.setProperty("range.type", this.rangeType != null ? this.rangeType : "today");
        prop.setProperty("range.start.date", this.startDate != null ? this.startDate : "");
        prop.setProperty("range.end.date", this.endDate != null ? this.endDate : "");
        prop.setProperty("range.count", String.valueOf(this.count));
        
        try (OutputStream output = new FileOutputStream(configFilePath)) {
            prop.store(output, "Information Configuration");
        }
        
        // 输出配置文件的绝对路径
        File configFile = new File(configFilePath);
        log.info("资讯配置已保存至: {}", configFile.getAbsolutePath());
    }
}