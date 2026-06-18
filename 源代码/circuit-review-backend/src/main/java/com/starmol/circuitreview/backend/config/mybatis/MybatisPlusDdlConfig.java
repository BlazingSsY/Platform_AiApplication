//package com.starmol.circuitreview.backend.config.mybatis;
//
//import com.baomidou.mybatisplus.extension.ddl.IDdl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.io.StringReader;
//import java.sql.ShardingKey;
//import java.util.Arrays;
//import java.util.List;
//import java.util.function.Consumer;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import java.io.IOException;
//
//@Component
//public class MybatisPlusDdlConfig implements IDdl {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Value("${migration.sql.path}")
//    private String migrationSqlFilePath;
//
//    @Override
//    public void runScript(Consumer<DataSource> consumer) {
//        consumer.accept(dataSource);
//    }
//
//    /**
//     * 获取要执行的SQL脚本文件列表
//     */
//    @Override
//    public List<String> getSqlFiles() {
//        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        try {
//            Resource[] resources = resolver.getResources("classpath:" + migrationSqlFilePath + "/*");
//            return Arrays.stream(resources)
//                    .map(resource -> migrationSqlFilePath + "/" + resource.getFilename())
//                    .toList();
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to load SQL files from db/migration", e);
//        }
//    }
//
//}
