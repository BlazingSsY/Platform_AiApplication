package com.starmol.sourcecodereview.config.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.sourcecodereview.common.UserMetaData;
import com.starmol.sourcecodereview.utils.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Optional;

@EnableTransactionManagement
@Configuration
@Slf4j
public class MybatisPlusConfig {

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandle() {
        JacksonTypeHandler.setObjectMapper(objectMapper);
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createUser", Long.class, Optional.ofNullable(HttpRequestUtil.getUser()).orElse(new UserMetaData() {
                }).getId());
                this.strictInsertFill(metaObject, "updateUser", Long.class, Optional.ofNullable(HttpRequestUtil.getUser()).orElse(new UserMetaData() {
                }).getId());
                this.strictInsertFill(metaObject, "createDate", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateDate", LocalDateTime.class, LocalDateTime.now());
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateUser", Long.class, Optional.ofNullable(HttpRequestUtil.getUser()).orElse(new UserMetaData() {
                }).getId());
                this.strictUpdateFill(metaObject, "updateDate", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }

    @Bean
    public IdentifierGenerator configIdentifierGenerator() {
        return new IdGenerator();
    }

    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(-1L);
        paginationInterceptor.setDbType(DbType.POSTGRE_SQL);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setOptimizeJoin(true);
        return paginationInterceptor;
    }
}
