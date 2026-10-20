package com.accounting.platform.storage;

import com.accounting.platform.common.InvalidOperationException;
import com.accounting.platform.config.MinioProperties;
import com.accounting.platform.config.StorageProperties;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

  private final S3Client s3Client;
  private final MinioProperties minioProperties;
  private final StorageProperties storageProperties;

  public String store(MultipartFile file) {
    validateFile(file);

    String originalName = file.getOriginalFilename();
    String extension = extractExtension(originalName);
    String storedName = UUID.randomUUID() + extension;

    try {
      PutObjectRequest putRequest =
          PutObjectRequest.builder()
              .bucket(minioProperties.bucket())
              .key(storedName)
              .contentType(file.getContentType())
              .build();

      s3Client.putObject(
          putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
      log.info("Файл сохранен в MinIO: {} ({} bytes)", storedName, file.getSize());
      return storedName;
    } catch (IOException ex) {
      throw new InvalidOperationException("Не удалось сохранить файл в MinIO: " + ex.getMessage());
    }
  }

  public Resource load(String storedName) {
    try {
      GetObjectRequest getRequest =
          GetObjectRequest.builder().bucket(minioProperties.bucket()).key(storedName).build();

      ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getRequest);
      return new InputStreamResource(s3Object);
    } catch (NoSuchKeyException ex) {
      throw new InvalidOperationException("Файл не найден в хранилище");
    } catch (Exception ex) {
      throw new InvalidOperationException("Не удалось прочитать файл из MinIO: " + ex.getMessage());
    }
  }

  public void delete(String storedName) {
    try {
      DeleteObjectRequest deleteRequest =
          DeleteObjectRequest.builder().bucket(minioProperties.bucket()).key(storedName).build();
      s3Client.deleteObject(deleteRequest);
      log.info("Файл удален из MinIO: {}", storedName);
    } catch (Exception ex) {
      log.warn("Не удалось удалить файл {} из MinIO: {}", storedName, ex.getMessage());
    }
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new InvalidOperationException("Файл не может быть пустым");
    }
    if (file.getSize() > storageProperties.maxFileSizeBytes()) {
      throw new InvalidOperationException(
          "Размер файла превышает лимит "
              + (storageProperties.maxFileSizeBytes() / 1024 / 1024)
              + " MB");
    }
    String contentType = file.getContentType();
    if (contentType == null || !storageProperties.allowedMimeTypes().contains(contentType)) {
      throw new InvalidOperationException(
          "Недопустимый тип файла: "
              + contentType
              + ". Разрешены: "
              + storageProperties.allowedMimeTypes());
    }
  }

  private String extractExtension(String filename) {
    if (filename == null) {
      return "";
    }
    int dot = filename.lastIndexOf('.');
    if (dot < 0 || dot == filename.length() - 1) {
      return "";
    }
    String ext = filename.substring(dot).toLowerCase();
    if (ext.length() > 10 || !ext.matches("\\.[a-z0-9]+")) {
      return "";
    }
    return ext;
  }
}
