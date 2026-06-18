package com.starmol.portal.backend.service.user.impl;


import org.apache.commons.lang3.RandomUtils;
import org.patchca.background.SingleColorBackgroundFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.ConfigurableFilterFactory;
import org.patchca.filter.library.AbstractImageOp;
import org.patchca.filter.library.WobbleImageOp;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.text.renderer.BestFitTextRenderer;
import org.patchca.text.renderer.TextRenderer;
import org.patchca.word.RandomWordFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.starmol.portal.backend.bean.bo.ImageCode;
import com.starmol.portal.backend.service.user.ImageCodeGenerator;

@Service
public class ImageCodeGeneratorImpl implements ImageCodeGenerator, InitializingBean {

    private ConfigurableCaptchaService configurableCaptchaService;

    public ImageCode generate() {
        // 得到验证码对象,有验证码图片和验证码字符串
        Captcha captcha = configurableCaptchaService.getCaptcha();
        return new ImageCode(captcha.getImage(), captcha.getChallenge(), Duration.ofMinutes(5));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        configurableCaptchaService = new ConfigurableCaptchaService();

        setFontColor();
        setFont();
        setWord();
        setFilter();
        setTextRender();
        setBackground();

        // 验证码图片的大小
        configurableCaptchaService.setWidth(200);
        configurableCaptchaService.setHeight(50);
    }

    private void setBackground() {
        final NoiseBackgroundFactory noiseBackgroundFactory = new NoiseBackgroundFactory();
        configurableCaptchaService.setBackgroundFactory(noiseBackgroundFactory);
    }

    private void setFontColor() {
        // 颜色创建工厂,使用RGB
        configurableCaptchaService.setColorFactory(new SingleColorFactory(new Color(142, 143, 152)));
    }

    private void setTextRender() {
        // 文字渲染器设置
        TextRenderer textRenderer = new BestFitTextRenderer();
        textRenderer.setBottomMargin(3);
        textRenderer.setTopMargin(3);
        configurableCaptchaService.setTextRenderer(textRenderer);
    }

    private void setFilter() {
        // 图片滤镜设置
        ConfigurableFilterFactory filterFactory = new ConfigurableFilterFactory();
        List<BufferedImageOp> filters = new ArrayList<>();
        WobbleImageOp wobbleImageOp = new WobbleImageOp();
        wobbleImageOp.setEdgeMode(AbstractImageOp.EDGE_MIRROR);
        wobbleImageOp.setxScale(2.0);
        wobbleImageOp.setyScale(2.0);
//        wobbleImageOp.setxWavelength(0.5);
        wobbleImageOp.setyWavelength(0.5);
        wobbleImageOp.setxAmplitude(1.0);
        wobbleImageOp.setyAmplitude(1.0);
        filters.add(wobbleImageOp);
        filterFactory.setFilters(filters);
        configurableCaptchaService.setFilterFactory(filterFactory);
    }

    private void setWord() {
        // 随机字符生成器,去除掉容易混淆的字母和数字,如o和0等
        RandomWordFactory wordFactory = new RandomWordFactory();
        wordFactory.setCharacters("ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnpqrstuvwxyz123456789");
        wordFactory.setMaxLength(4);
        wordFactory.setMinLength(4);
        configurableCaptchaService.setWordFactory(wordFactory);
    }

    private void setFont() {
        // 随机字体生成器
        RandomFontFactory fontFactory = new RandomFontFactory();
        fontFactory.setMaxSize(32);
        fontFactory.setMinSize(28);
        configurableCaptchaService.setFontFactory(fontFactory);
    }

    private static class NoiseBackgroundFactory extends SingleColorBackgroundFactory {

        @Override
        public void fillBackground(BufferedImage dest) {

            final int width = dest.getWidth();
            final int height = dest.getHeight();

            Graphics2D g = (Graphics2D) dest.getGraphics();
            g.setBackground(Color.WHITE);
            g.fillRect(0, 0, width, height);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < RandomUtils.nextInt(0, 5) + 1; i++) {
                CubicCurve2D curve = new CubicCurve2D.Double(RandomUtils.nextInt(0, width), RandomUtils.nextInt(0, height),
                        RandomUtils.nextInt(0, width), RandomUtils.nextInt(0, height),
                        RandomUtils.nextInt(0, width), RandomUtils.nextInt(0, height),
                        RandomUtils.nextInt(0, width), RandomUtils.nextInt(0, height));
                //设置干扰线的颜色
                g.setPaint(new Color(RandomUtils.nextInt(0, 255) + 1, RandomUtils.nextInt(0, 255) + 1, RandomUtils.nextInt(0, 255) + 1));
                g.draw(curve);
            }

        }

    }

}