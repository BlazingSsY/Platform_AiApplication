package com.starmol.circuitreview.backend.service.circuitreview.feign;

import com.starmol.circuitreview.backend.bean.bo.ResponseDataBO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "circuitReviewClient", url = "${review.service.url}")
public interface ReviewClient {
    @PostMapping("/review")
    ResponseWrapper<Map<String, Map<String, Object>>> review(@RequestBody Map<String, String> params);
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseWrapper<Map<String, Object>> uploadFile(@RequestPart("file") MultipartFile file);

    @DeleteMapping(value = "/files")
    ResponseWrapper<Void> deleteFile(@RequestParam("filename") String filename);
    
    @GetMapping("/rules")
    ResponseWrapper<Map<String, Object>> getRules();

    @GetMapping("/log")
    ResponseDataBO<Object> getLog(@RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId, @RequestParam(name = "num", defaultValue = "5000") Integer num, @RequestParam(name = "log_name", defaultValue = "debug") String logName);
}
