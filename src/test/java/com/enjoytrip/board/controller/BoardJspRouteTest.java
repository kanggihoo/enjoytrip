package com.enjoytrip.board.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class BoardJspRouteTest {

    private static final Path BOARD_VIEWS = Path.of("src/main/webapp/WEB-INF/views/board");

    @Test
    void writeFormPostsToCanonicalBoardsRoute() throws IOException {
        String jsp = Files.readString(BOARD_VIEWS.resolve("write.jsp"));

        assertThat(jsp).contains("method=\"post\" action=\"${pageContext.request.contextPath}/boards\"");
        assertThat(jsp).doesNotContain("/board/regist");
    }

    @Test
    void modifyFormPostsToCanonicalBoardRoute() throws IOException {
        String jsp = Files.readString(BOARD_VIEWS.resolve("modify.jsp"));

        assertThat(jsp).contains("method=\"post\" action=\"${pageContext.request.contextPath}/boards/${board.boardId}\"");
        assertThat(jsp).doesNotContain("/board/update");
    }

    @Test
    void detailDeleteUsesPostForm() throws IOException {
        String jsp = Files.readString(BOARD_VIEWS.resolve("detail.jsp"));

        assertThat(jsp).contains("method=\"post\" action=\"${pageContext.request.contextPath}/boards/${board.boardId}/delete\"");
        assertThat(jsp).doesNotContain("/board/delete");
    }
}
