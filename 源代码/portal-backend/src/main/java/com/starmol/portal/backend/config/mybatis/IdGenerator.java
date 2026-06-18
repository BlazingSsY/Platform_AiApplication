package com.starmol.portal.backend.config.mybatis;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.starmol.portal.backend.utils.IdWorker;

/**
 * @author huguojun
 */
public class IdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return IdWorker.getId();
    }

    @Override
    public String nextUUID(Object entity) {
        return String.valueOf(IdWorker.getId());
    }
}
