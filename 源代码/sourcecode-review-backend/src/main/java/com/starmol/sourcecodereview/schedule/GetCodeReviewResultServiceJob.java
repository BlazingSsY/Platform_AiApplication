package com.starmol.sourcecodereview.schedule;

import com.starmol.sourcecodereview.service.SourceCodeReviewResultService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yuexiaopeng
 * @since 2021-03-18
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class GetCodeReviewResultServiceJob {

    @Autowired
    private final SourceCodeReviewResultService sourceCodeReviewResultService;

    @Scheduled(cron = "${timer.cron.get-code-review-cron}")
    public void updateServiceFromNacos() {
        log.info("Start get code review result");
        sourceCodeReviewResultService.getAllCodeReviewResultAndUpdate();
    }
}
