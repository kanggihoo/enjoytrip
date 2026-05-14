package com.enjoytrip.board.mapper;

import com.enjoytrip.board.dto.Board;
import com.enjoytrip.support.AbstractMySqlContainerTest;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BoardMapperTest extends AbstractMySqlContainerTest {

    @Autowired
    BoardMapper boardMapper;

    @Test
    void selectSeedNotices() {
        List<Board> notices = boardMapper.selectBoardsByType("notice");

        assertThat(notices)
                .extracting(Board::getBoardId)
                .contains(1, 2);
    }

    @Test
    void insertIncreaseViewsUpdateAndDeleteBoard() {
        Board board = Board.builder()
                .type("free")
                .title("Mapper board")
                .content("Mapper content")
                .userId("ssafy")
                .build();

        boardMapper.insertBoard(board);

        assertThat(board.getBoardId()).isPositive();
        Board inserted = boardMapper.selectBoardById(board.getBoardId());
        assertThat(inserted.getTitle()).isEqualTo("Mapper board");
        assertThat(inserted.getViews()).isZero();

        boardMapper.increaseViews(board.getBoardId());

        Board viewed = boardMapper.selectBoardById(board.getBoardId());
        assertThat(viewed.getViews()).isEqualTo(1);

        board.setTitle("Mapper board updated");
        board.setContent("Mapper content updated");
        boardMapper.updateBoard(board);

        Board updated = boardMapper.selectBoardById(board.getBoardId());
        assertThat(updated.getTitle()).isEqualTo("Mapper board updated");
        assertThat(updated.getContent()).isEqualTo("Mapper content updated");

        boardMapper.deleteBoard(board.getBoardId());

        assertThat(boardMapper.selectBoardById(board.getBoardId())).isNull();
    }
}
