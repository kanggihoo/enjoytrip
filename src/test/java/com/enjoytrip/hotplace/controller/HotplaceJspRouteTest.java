package com.enjoytrip.hotplace.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

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
}
