package com.enjoytrip.board.mapper;

import java.util.List;

import com.enjoytrip.board.dto.Board;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {
    List<Board> selectBoardsByType(String type);
    Board selectBoardById(int boardId);
    void increaseViews(int boardId);
    int insertBoard(Board board);
    void updateBoard(Board board);
    void deleteBoard(int boardId);
}
