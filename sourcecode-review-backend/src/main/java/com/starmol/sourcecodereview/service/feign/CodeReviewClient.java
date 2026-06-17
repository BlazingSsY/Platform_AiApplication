package com.starmol.sourcecodereview.service.feign;

import com.starmol.sourcecodereview.bean.bo.CodeReviewRecheckBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRecheckListRequestBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRecheckResultSubmitBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewSelectRuleBO;
import com.starmol.sourcecodereview.bean.bo.ReviewSummaryBO;
import com.starmol.sourcecodereview.bean.bo.ResponseDataBO;
import com.starmol.sourcecodereview.bean.dto.FilterAllRulesDTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "codeReviewClient", url = "${review.service.code-url}")
public interface CodeReviewClient {
    @PostMapping(value = "/code/review/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseDataBO<Map<String, String>> uploadFile(@RequestPart("file") MultipartFile file, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId, @RequestParam("reviewId") String reviewId);

    @GetMapping("/code/review/execute/{reviewId}")
    ResponseDataBO<Object> review(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @GetMapping("/code/review/result/{reviewId}")
    String getResult(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId, @RequestParam("version") String version);

    @GetMapping("/code/review/download/url/{reviewId}")
    ResponseDataBO<Map<String, String>> getExportLink(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @GetMapping("/code/review/delete/{reviewId}")
    ResponseDataBO<Object> deleteFile(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @PostMapping("/code/review/rule/all")
    ResponseDataBO<Object> getAllRules(@RequestBody FilterAllRulesDTO filterAllRulesDTO, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @PostMapping("/code/review/rule/select")
    ResponseDataBO<Object> selectRules(@RequestBody CodeReviewSelectRuleBO params, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @PostMapping("/code/review/rule/details")
    ResponseDataBO<Object> getRulesDetails(@RequestBody CodeReviewSelectRuleBO params, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @GetMapping("/code/review/metadata")
    ResponseDataBO<Object> getMetaData(@RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @PostMapping("/code/review/summary/msg")
    ResponseDataBO<Object> getReviewSummary(@RequestBody ReviewSummaryBO params, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);;

    @GetMapping("/code/review/stop/{reviewId}")
    ResponseDataBO<Object> stopReview(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @GetMapping("/code/review/wait/task/{reviewId}")
    ResponseDataBO<Object> getWaitTask(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @GetMapping("/code/review/process/{reviewId}")
    ResponseDataBO<Object> getReviewProcess(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @GetMapping("/code/review/source/code/{reviewId}")
    ResponseDataBO<Object> getSourceCode(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId, @RequestParam("fileName") String fileName, @RequestParam("offset") String offset,@RequestParam("version") String version);;

    @GetMapping("/code/review/log")
    ResponseDataBO<Object> getLog(@RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId,@RequestParam("num") Integer num);;

    @GetMapping("/code/review/version/list/{reviewId}")
    ResponseDataBO<Object> getVersionList(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @PostMapping("/code/review/recheck")
    ResponseDataBO<Object> reviewRecheck(@RequestBody CodeReviewRecheckBO params, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @PostMapping("/code/review/recheck/list")
    ResponseDataBO<Object> reviewRecheckList(@RequestBody CodeReviewRecheckListRequestBO params, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);

    @GetMapping("/code/review/recheck/detail/{reviewId}")
    String getRecheckDetail(@PathVariable("reviewId") String reviewId, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId, @RequestParam("version") String version, @RequestParam("curUserRoleType") Integer curUserRoleType);

    @PostMapping("/code/review/recheck/result/submit")
    ResponseDataBO<Object> reviewRecheckResultSubmit(@RequestBody CodeReviewRecheckResultSubmitBO params, @RequestHeader("X-Trace-Id") String traceId, @RequestHeader("X-User-Id") String userId);



}
