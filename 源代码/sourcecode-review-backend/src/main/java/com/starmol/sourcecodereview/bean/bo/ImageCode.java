package com.starmol.sourcecodereview.bean.bo;

import java.awt.image.BufferedImage;
import java.time.Duration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "存放数字生成的图片及相关信息")
public class ImageCode {

    private final String code;
    /**
     * 过期时间
     */
    private final Duration expireTime;
    private BufferedImage image;

    public ImageCode(BufferedImage image, String code, Duration duration) {
        this.image = image;
        this.code = code;
        this.expireTime = duration;
    }
}