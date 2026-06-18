package com.starmol.circuitreview.backend.bean.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.Data;


@Data
public class RulePageVO<T> extends Page<T> {
    private long selectSize;

    public RulePageVO(long current, long size) {
        super(current, size, 0L);
    }
}

