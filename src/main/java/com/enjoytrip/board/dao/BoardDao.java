package com.enjoytrip.board.dao;

import com.enjoytrip.board.dto.Board;
import java.sql.SQLException;
import java.util.List;

public interface BoardDao {
    List<Board> selectByType(String type) throws SQLException;
    Board selectById(int boardId) throws SQLException;
    int insert(Board board) throws SQLException;
    void update(Board board) throws SQLException;
    void delete(int boardId) throws SQLException;
    void incrementViews(int boardId) throws SQLException;
}
