package com.enjoytrip.hotplace.controller;

import com.enjoytrip.config.AuthInterceptor;
import com.enjoytrip.config.RequestLoggingInterceptor;
import com.enjoytrip.config.WebMvcConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HotplaceJspRouteTest {

    private static final Path HOTPLACE_VIEWS = Path.of("src/main/webapp/WEB-INF/views/hotplace");

    @Test
    void writeFormPostsToCanonicalHotplacesRoute() throws IOException {
        String jsp = Files.readString(HOTPLACE_VIEWS.resolve("write.jsp"));

        assertThat(jsp).contains("action=\"${pageContext.request.contextPath}/hotplaces\"");
        assertThat(jsp).doesNotContain("/hotplace/regist");
    }

    @Test
    void modifyFormPostsToCanonicalHotplaceRoute() throws IOException {
        String jsp = Files.readString(HOTPLACE_VIEWS.resolve("modify.jsp"));

        assertThat(jsp).contains("action=\"${pageContext.request.contextPath}/hotplaces/${hotplace.hotplaceId}\"");
        assertThat(jsp).doesNotContain("/hotplace/update");
    }

    @Test
    void detailDeleteUsesPostForm() throws IOException {
        String jsp = Files.readString(HOTPLACE_VIEWS.resolve("detail.jsp"));

        assertThat(jsp).contains("action=\"${pageContext.request.contextPath}/hotplaces/${hotplace.hotplaceId}/delete\"");
        assertThat(jsp).contains("method=\"post\"");
        assertThat(jsp).doesNotContain("/hotplace/delete");
    }

    @Test
    void webMvcConfigUsesConfiguredUploadDirectoryForStaticUploads() {
        Path configuredUploadDir = Path.of("build", "..", "custom-uploads");
        WebMvcConfig config = new WebMvcConfig(
                mock(AuthInterceptor.class),
                mock(RequestLoggingInterceptor.class),
                configuredUploadDir.toString());

        String uploadResourceLocation = (String) ReflectionTestUtils.getField(config, "uploadResourceLocation");

        String expectedLocation = configuredUploadDir.toAbsolutePath().normalize().toUri().toString();
        assertThat(uploadResourceLocation)
                .isEqualTo(expectedLocation.endsWith("/") ? expectedLocation : expectedLocation + "/");
    }
}
