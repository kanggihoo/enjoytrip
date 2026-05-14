package com.enjoytrip.hotplace.service;

import com.enjoytrip.common.exception.BadRequestException;
import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final Map<String, String> CONTENT_TYPE_EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/gif", ".gif",
            "image/webp", ".webp"
    );

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        String extension = resolveExtension(file);
        String savedName = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(uploadDir);
            file.transferTo(uploadDir.resolve(savedName));
            return "uploads/" + savedName;
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.FILE_UPLOAD_ERROR, e);
        }
    }

    private String resolveExtension(MultipartFile file) {
        String contentTypeExtension = CONTENT_TYPE_EXTENSIONS.get(normalizeContentType(file.getContentType()));
        if (contentTypeExtension == null) {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            String extension = originalName.substring(originalName.lastIndexOf('.')).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(extension) && matchesContentType(extension, contentTypeExtension)) {
                return extension;
            }
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }

        return contentTypeExtension;
    }

    private String normalizeContentType(String contentType) {
        return contentType == null ? "" : contentType.toLowerCase(Locale.ROOT);
    }

    private boolean matchesContentType(String extension, String contentTypeExtension) {
        return extension.equals(contentTypeExtension)
                || (contentTypeExtension.equals(".jpg") && extension.equals(".jpeg"));
    }
}
