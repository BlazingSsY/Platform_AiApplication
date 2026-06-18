package com.starmol.sso.server.actuator;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;


/**
 * 模拟检测第三方验证用户名密码接口
 */
@Component
public class CustomUPMSApiServerHealthEndpoint extends AbstractHealthIndicator {

    //======================================================

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        builder.up();
//		OkHttpResponse okHttpResponse = okHttpService.get("");
//		if (okHttpResponse.getStatus() == HttpStatus.OK.value()) {
//			builder.up();
//		} else {
//			builder.down();
//		}
    }
}
