package com.starmol.portal.backend.controller.common;

import com.starmol.portal.backend.aop.Permit;
import com.starmol.portal.backend.common.Permission;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Tag(name = "版本信息")
@RestController
@RequestMapping("/common/v1/version")
public class VersionController {

    private static final String VERSION_FILE_PATH = "/version.txt";

    /**
     * 获取工程版本信息
     * @return 版本号字符串，文件不存在时返回 "unknown"
     */
    @GetMapping
    @Permit(Permission.ALL)
    public ResponseEntity<String> getVersion() {
        try {
            String version = Files.readString(Path.of(VERSION_FILE_PATH)).trim();
            return ResponseEntity.ok(version);
        } catch (IOException e) {
            log.warn("Version file not found or unreadable: {}", e.getMessage());
            return ResponseEntity.ok("unknown");
        }
    }
}