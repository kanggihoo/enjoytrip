package com.enjoytrip.board.service;

import com.enjoytrip.board.dto.Board;
import com.enjoytrip.board.mapper.BoardMapper;
import com.enjoytrip.common.exception.ForbiddenException;
import com.enjoytrip.common.exception.NotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BoardServiceImplTest {

    private final BoardMapper mapper = mock(BoardMapper.class);
    private final BoardService service = new BoardServiceImpl(mapper);

    @Test
    void modifyThrowsNotFoundWhenBoardMissing() {
        when(mapper.selectBoardById(1)).thenReturn(null);
        Board board = Board.builder().boardId(1).title("t").content("c").build();

        assertThatThrownBy(() -> service.modify(board, "ssafy"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void modifyThrowsForbiddenWhenOwnerDiffers() {
        when(mapper.selectBoardById(1)).thenReturn(Board.builder().boardId(1).userId("owner").build());
        Board board = Board.builder().boardId(1).title("t").content("c").build();

        assertThatThrownBy(() -> service.modify(board, "other"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void modifyThrowsForbiddenWhenLoginUserIsNull() {
        when(mapper.selectBoardById(1)).thenReturn(Board.builder().boardId(1).userId("owner").build());
        Board board = Board.builder().boardId(1).title("t").content("c").build();

        assertThatThrownBy(() -> service.modify(board, null))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void getForEditDoesNotIncreaseViews() {
        when(mapper.selectBoardById(1)).thenReturn(Board.builder().boardId(1).userId("ssafy").views(3).build());

        service.getForEdit(1, "ssafy");

        verify(mapper, never()).increaseViews(1);
    }

    @Test
    void deleteChecksOwnerAndDeletes() {
        when(mapper.selectBoardById(1)).thenReturn(Board.builder().boardId(1).userId("ssafy").build());

        service.remove(1, "ssafy");

        verify(mapper).deleteBoard(1);
    }
}
