package com.accounting.platform.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketAlreadyExistsException;
import software.amazon.awssdk.services.s3.model.BucketAlreadyOwnedByYouException;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioBucketInitializer implements CommandLineRunner {

    private final S3Client s3Client;
    private final MinioProperties minioProperties;

    @Override
    public void run(String... args) {
        String bucket = minioProperties.bucket();
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            log.info("MinIO: бакет '{}' уже существует", bucket);
        } catch (Exception e) {
            log.info("MinIO: бакет '{}' не найден, создаем...", bucket);
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
                log.info("MinIO: бакет '{}' успешно создан", bucket);
            } catch (BucketAlreadyExistsException | BucketAlreadyOwnedByYouException ex) {
                log.info("MinIO: бакет '{}' уже существует", bucket);
            } catch (Exception ex) {
                log.error("MinIO: ОШИБКА при создании бакета '{}'. Проверьте подключение к Silo.", bucket, ex);
                throw new RuntimeException("MinIO initialization failed", ex);
            }
        }
    }
}
