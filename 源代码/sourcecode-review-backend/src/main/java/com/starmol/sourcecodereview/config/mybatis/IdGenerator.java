package com.starmol.sourcecodereview.config.mybatis;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.starmol.sourcecodereview.utils.IdWorker;

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
