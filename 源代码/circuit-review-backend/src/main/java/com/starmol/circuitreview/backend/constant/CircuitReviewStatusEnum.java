package com.starmol.circuitreview.backend.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

public enum CircuitReviewStatusEnum implements IEnum<Integer> {
    IN_PROGRESS(1, "正在审查"),
    FINISHED(2, "审查完成"),
    FAILED(3, "审查失败");

    private final Integer value;
    @Getter
    private final String name;

    CircuitReviewStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
