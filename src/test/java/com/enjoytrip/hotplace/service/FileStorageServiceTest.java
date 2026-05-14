package com.enjoytrip.hotplace.service;

import com.enjoytrip.common.exception.BadRequestException;
import com.enjoytrip.common.exception.FileStorageException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    @TempDir
    Path uploadDir;

    @Test
    void storeReturnsEmptyStringWhenFileIsEmpty() {
        FileStorageService service = new FileStorageService(uploadDir.toString());
        MockMultipartFile file = new MockMultipartFile("image", "empty.jpg", "image/jpeg", new byte[0]);

        String savedPath = service.store(file);

        assertThat(savedPath).isEmpty();
    }

    @Test
    void storeSavesFileWithOriginalExtensionAndReturnsUploadsPath() throws Exception {
        FileStorageService service = new FileStorageService(uploadDir.toString());
        MockMultipartFile file = new MockMultipartFile("image", "place.jpg", "image/jpeg", "image".getBytes());

        String savedPath = service.store(file);

        assertThat(savedPath).startsWith("uploads/").endsWith(".jpg");
        assertThat(Files.exists(uploadDir.resolve(savedPath.substring("uploads/".length())))).isTrue();
    }

    @Test
    void storeRejectsHtmlUpload() {
        FileStorageService service = new FileStorageService(uploadDir.toString());
        MockMultipartFile file = new MockMultipartFile("image", "place.html", "text/html", "html".getBytes());

        assertThatThrownBy(() -> service.store(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void storeRejectsSvgUpload() {
        FileStorageService service = new FileStorageService(uploadDir.toString());
        MockMultipartFile file = new MockMultipartFile("image", "place.svg", "image/svg+xml", "svg".getBytes());

        assertThatThrownBy(() -> service.store(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void storeRejectsAllowedExtensionWithUnsupportedContentType() {
        FileStorageService service = new FileStorageService(uploadDir.toString());
        MockMultipartFile file = new MockMultipartFile("image", "place.jpg", "text/html", "html".getBytes());

        assertThatThrownBy(() -> service.store(file))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void storeWrapsIoFailureInFileStorageException() throws Exception {
        Path uploadFile = uploadDir.resolve("not-directory");
        Files.writeString(uploadFile, "blocked");
        FileStorageService service = new FileStorageService(uploadFile.toString());
        MockMultipartFile file = new MockMultipartFile("image", "place.jpg", "image/jpeg", "image".getBytes());

        assertThatThrownBy(() -> service.store(file))
                .isInstanceOf(FileStorageException.class);
    }
}
