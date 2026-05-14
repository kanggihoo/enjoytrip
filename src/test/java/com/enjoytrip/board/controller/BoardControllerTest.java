package com.enjoytrip.board.controller;

import com.enjoytrip.board.dto.Board;
import com.enjoytrip.board.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BoardService boardService;

    @Test
    void listReturnsBoardListView() throws Exception {
        List<Board> boards = List.of(Board.builder().boardId(1).type("free").title("title").build());
        when(boardService.getList("free")).thenReturn(boards);

        mockMvc.perform(get("/boards")
                        .sessionAttr("loginUser", "ssafy")
                        .param("type", "free"))
                .andExpect(status().isOk())
                .andExpect(view().name("board/list"))
                .andExpect(model().attribute("boardList", boards))
                .andExpect(model().attribute("type", "free"));
    }

    @Test
    void writeUsesSessionUserAndRedirectsToDetail() throws Exception {
        when(boardService.write(any(Board.class))).thenAnswer(invocation -> {
            Board board = invocation.getArgument(0);
            board.setBoardId(7);
            return 7;
        });

        mockMvc.perform(post("/boards")
                        .sessionAttr("loginUser", "ssafy")
                        .param("type", "free")
                        .param("title", "title")
                        .param("content", "content")
                        .param("userId", "attacker"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/7?type=free"));

        var boardCaptor = forClass(Board.class);
        verify(boardService).write(boardCaptor.capture());
        assertThat(boardCaptor.getValue().getUserId()).isEqualTo("ssafy");
    }

    @Test
    void modifyUsesSessionUserForOwnership() throws Exception {
        mockMvc.perform(post("/boards/1")
                        .sessionAttr("loginUser", "ssafy")
                        .param("type", "free")
                        .param("title", "updated")
                        .param("content", "content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/1?type=free"));

        verify(boardService).modify(any(Board.class), eq("ssafy"));
    }

    @Test
    void editRedirectsToDetailWhenSessionUserIsNotOwner() throws Exception {
        when(boardService.getDetail(1)).thenReturn(Board.builder().boardId(1).userId("owner").build());

        mockMvc.perform(get("/boards/1/edit")
                        .sessionAttr("loginUser", "other")
                        .param("type", "free"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/1?type=free"));
    }

    @Test
    void deleteUsesSessionUserForOwnership() throws Exception {
        mockMvc.perform(post("/boards/1/delete")
                        .sessionAttr("loginUser", "ssafy")
                        .param("type", "free"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards?type=free"));

        verify(boardService).remove(1, "ssafy");
    }

    @Test
    void legacyListRedirectsToCanonicalList() throws Exception {
        mockMvc.perform(get("/board/list").param("type", "notice"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards?type=notice"));
    }
}
