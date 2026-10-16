package com.accounting.platform.storage;

import com.accounting.platform.common.InvalidOperationException;
import com.accounting.platform.config.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final StorageProperties storageProperties;

    public String store(MultipartFile file) {
        validateFile(file);

        try {
            Path uploadPath = Paths.get(storageProperties.uploadDir()).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalName = file.getOriginalFilename();
            String extension = extractExtension(originalName);
            String storedName = UUID.randomUUID() + extension;

            Path target = uploadPath.resolve(storedName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            log.info("Файл сохранён: {} ({} bytes)", storedName, file.getSize());
            return storedName;
        } catch (IOException ex) {
            throw new InvalidOperationException("Не удалось сохранить файл: " + ex.getMessage());
        }
    }

    public Resource load(String storedName) {
        try {
            Path uploadPath = Paths.get(storageProperties.uploadDir()).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(storedName).normalize();

            if (!filePath.startsWith(uploadPath)) {
                throw new InvalidOperationException("Некорректный путь к файлу");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new InvalidOperationException("Файл не найден или недоступен для чтения");
            }
            return resource;
        } catch (IOException ex) {
            throw new InvalidOperationException("Не удалось прочитать файл: " + ex.getMessage());
        }
    }

    public void delete(String storedName) {
        try {
            Path uploadPath = Paths.get(storageProperties.uploadDir()).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(storedName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.warn("Не удалось удалить файл {}: {}", storedName, ex.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidOperationException("Файл не может быть пустым");
        }
        if (file.getSize() > storageProperties.maxFileSizeBytes()) {
            throw new InvalidOperationException(
                    "Размер файла превышает лимит " + (storageProperties.maxFileSizeBytes() / 1024 / 1024) + " MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !storageProperties.allowedMimeTypes().contains(contentType)) {
            throw new InvalidOperationException(
                    "Недопустимый тип файла: " + contentType +
                            ". Разрешены: " + storageProperties.allowedMimeTypes());
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
