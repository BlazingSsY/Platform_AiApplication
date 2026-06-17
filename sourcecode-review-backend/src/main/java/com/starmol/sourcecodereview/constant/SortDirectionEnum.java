package com.starmol.sourcecodereview.constant;

import lombok.Getter;

/**
 * 排序方向枚举
 *
 * @author system
 * @date 2025-01-07
 */
public enum SortDirectionEnum {
    ASC("asc", "升序"),
    DESC("desc", "降序");

    @Getter
    private final String direction;
    @Getter
    private final String description;

    SortDirectionEnum(String direction, String description) {
        this.direction = direction;
        this.description = description;
    }

    /**
     * 根据方向字符串获取枚举
     */
    public static SortDirectionEnum fromDirection(String direction) {
        for (SortDirectionEnum dir : values()) {
            if (dir.getDirection().equalsIgnoreCase(direction)) {
                return dir;
            }
        }
        return null;
    }
}
