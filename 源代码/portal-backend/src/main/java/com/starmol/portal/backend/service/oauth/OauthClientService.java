package com.starmol.portal.backend.service.oauth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.portal.backend.bean.dto.oauth.OauthClientCreateRequestParam;
import com.starmol.portal.backend.bean.vo.oauth.SsoClients;
import com.starmol.portal.backend.model.OauthClient;
import com.starmol.portal.backend.service.base.BaseCascadeService;

import java.util.List;

/**
 * @author huguojun
 */
public interface OauthClientService extends BaseCascadeService<OauthClient> {
    IPage<OauthClient> findPageByParam(Page<OauthClient> objectPage, String clientName, String clientId, Integer stateEnum);

    OauthClient findOneById(Long id);

    OauthClient create(OauthClientCreateRequestParam param);

    void batchDelete(List<Long> idList);

    void batchUpdateState(Integer stateEnum, List<Long> idList);

    SsoClients getClientList();
}
