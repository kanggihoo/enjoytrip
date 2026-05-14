package com.enjoytrip.hotplace.service;

import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String savedName = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(uploadDir);
            file.transferTo(uploadDir.resolve(savedName));
            return "uploads/" + savedName;
        } catch (IOException e) {
            throw new FileStorageException(ErrorCode.FILE_UPLOAD_ERROR, e);
        }
    }
}
