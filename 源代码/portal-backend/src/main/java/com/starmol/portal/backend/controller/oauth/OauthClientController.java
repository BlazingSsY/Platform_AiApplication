package com.starmol.portal.backend.controller.oauth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.portal.backend.bean.dto.oauth.OauthClientBatchUpdateStateRequestParam;
import com.starmol.portal.backend.bean.dto.oauth.OauthClientCreateRequestParam;
import com.starmol.portal.backend.bean.dto.oauth.OauthClientIdRequestParam;
import com.starmol.portal.backend.bean.vo.oauth.SsoClients;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.model.OauthClient;
import com.starmol.portal.backend.service.oauth.OauthClientService;
import com.starmol.portal.backend.service.redis.StringRedisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping("/v1/sso/client")
@RequiredArgsConstructor
public class OauthClientController {

    private static final String REDIS_NOT_CONFIGURED_MSG = "SSO OAuth功能未配置，请先配置Redis连接（设置 REDIS_HOST 环境变量）";

    private final OauthClientService oauthClientService;

    @Autowired(required = false)
    private StringRedisService<String, String> stringRedisService;

    /**
     * 检查 Redis 是否已配置，未配置则记录告警日志并返回 false。
     */
    private boolean isOauthAvailable(String methodName) {
        if (stringRedisService == null) {
            log.warn("[OAuth不可用] 接口={}，原因=Redis未配置或不可达，返回提示={}", methodName, REDIS_NOT_CONFIGURED_MSG);
            return false;
        }
        return true;
    }

    @GetMapping("/page")
    public ResponseWrapper<IPage<OauthClient>> page(@RequestParam(required = false, defaultValue = "1") Long pageNumber,
                                                    @RequestParam(required = false, defaultValue = "10") Long pageSize,
                                                    @RequestParam(required = false) String clientName,
                                                    @RequestParam(required = false) String clientId,
                                                    @RequestParam(required = false) Integer stateEnum) {
        if (!isOauthAvailable("page")) {
            return ResponseWrapper.fail(REDIS_NOT_CONFIGURED_MSG);
        }
        IPage<OauthClient> pageResult = oauthClientService.findPageByParam(new Page<>(pageNumber, pageSize), clientName, clientId, stateEnum);
        return ResponseWrapper.success(pageResult);
    }

    @GetMapping("/detail")
    public ResponseWrapper<OauthClient> detailByApplicationId(@RequestParam Long applicationId) {
        if (!isOauthAvailable("detail")) {
            return ResponseWrapper.fail(REDIS_NOT_CONFIGURED_MSG);
        }
        OauthClient oauthClient = oauthClientService.findOneById(applicationId);
        return ResponseWrapper.success(oauthClient);
    }

    @PostMapping("/create")
    public ResponseWrapper<String> create(@Valid @RequestBody OauthClientCreateRequestParam param) {
        if (!isOauthAvailable("create")) {
            return ResponseWrapper.fail(REDIS_NOT_CONFIGURED_MSG);
        }
        oauthClientService.create(param);
        return ResponseWrapper.success();
    }

    @PostMapping("/batchDelete")
    public ResponseWrapper<String> batchDelete(@Valid @RequestBody OauthClientIdRequestParam param) {
        if (!isOauthAvailable("batchDelete")) {
            return ResponseWrapper.fail(REDIS_NOT_CONFIGURED_MSG);
        }
        oauthClientService.batchDelete(param.getIdList());
        return ResponseWrapper.success();
    }

    @PostMapping("/batchUpdateState")
    public ResponseWrapper<String> batchUpdateState(@Valid @RequestBody OauthClientBatchUpdateStateRequestParam param) {
        if (!isOauthAvailable("batchUpdateState")) {
            return ResponseWrapper.fail(REDIS_NOT_CONFIGURED_MSG);
        }
        oauthClientService.batchUpdateState(param.getStateEnum(), param.getIdList());
        return ResponseWrapper.success();
    }

    @GetMapping("/list")
    public ResponseWrapper<SsoClients> list() {
        if (!isOauthAvailable("list")) {
            return ResponseWrapper.fail(REDIS_NOT_CONFIGURED_MSG);
        }
        SsoClients clients = oauthClientService.getClientList();
        return ResponseWrapper.success(clients);
    }

}
