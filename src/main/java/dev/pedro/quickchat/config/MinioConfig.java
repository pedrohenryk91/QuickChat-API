package dev.pedro.quickchat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class MinioConfig {

    private String url;

    private String accessKey;

    private String accessSecret;

    public MinioConfig(
        @Value("${MINIO_URL:http://localhost:9000}")String url,
        @Value("${MINIO_ACCESS_KEY:minioadmin}") String accessKey,
        @Value("${MINIO_ACCESS_SECRET:minioadmin}") String accessSecret
    ) {
        this.url = url;
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, accessSecret)
                .build();
    }

}
