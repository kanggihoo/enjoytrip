package com.enjoytrip.board.service;

import com.enjoytrip.board.dto.Board;
import java.sql.SQLException;
import java.util.List;

public interface BoardService {
    List<Board> getList(String type) throws SQLException;
    Board getDetail(int boardId) throws SQLException;
    int write(Board board) throws SQLException;
    void modify(Board board) throws SQLException;
    void remove(int boardId) throws SQLException;
}
