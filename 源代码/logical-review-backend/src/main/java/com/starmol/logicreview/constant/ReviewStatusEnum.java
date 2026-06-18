package com.starmol.logicreview.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

public enum ReviewStatusEnum  implements IEnum<Integer> {
    IN_PROGRESS(1, "正在审查"),
    FINISHED(2, "审查完成"),
    FAILED(3, "审查失败"),
    ERROR (4, "审查错误");

    private final Integer value;
    @Getter
    private final String name;

    ReviewStatusEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }
}
