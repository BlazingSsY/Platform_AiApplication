package com.starmol.circuitreview.backend.config;

import com.starmol.circuitreview.backend.utils.MinioUtils;
import io.minio.MinioClient;
import io.minio.SetBucketLifecycleArgs;
import io.minio.messages.Expiration;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.LifecycleRule;
import io.minio.messages.ResponseDate;
import io.minio.messages.RuleFilter;
import io.minio.messages.Status;
import io.minio.messages.Tag;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * MinIO configuration
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO server endpoint
     */
    private String endpoint;

    /**
     * MinIO access key (username)
     */
    private String accessKey;

    /**
     * MinIO secret key (password)
     */
    private String secretKey;

    /**
     * Whether to use secure connection (https)
     */
    private boolean secure = false;

    private String bucketName;

    /**
     * 设置桶的生命周期规则
     *
     * @param client 客户端
     * @param days   天数
     */
    private static void setBucketLifecycleRules(MinioClient client, String bucketName, Integer days) {
        Expiration expiration = new Expiration((ResponseDate) null, days, (Boolean) null);
        RuleFilter ruleFilter = new RuleFilter(null, null, new Tag("temp", "1"));
        LifecycleRule lifecycleRule = new LifecycleRule(Status.ENABLED, null, expiration, ruleFilter, "auto-delete-file-rule", null, null, null);
        LifecycleConfiguration lifecycleConfiguration = new LifecycleConfiguration(List.of(lifecycleRule));
        SetBucketLifecycleArgs args = SetBucketLifecycleArgs
                .builder()
                .bucket(bucketName)
                .config(lifecycleConfiguration)
                .build();
        try {
            client.setBucketLifecycle(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create and configure MinioClient bean
     *
     * @return Configured MinioClient
     */
    @Bean
    public MinioClient minioClient() {
        final MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        // 使用工具类确保存储桶存在
        MinioUtils.ensureBucketExists(minioClient, bucketName);
        setBucketLifecycleRules(minioClient, bucketName, 1);
        return minioClient;
    }
}
