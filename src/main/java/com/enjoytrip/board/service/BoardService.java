package com.enjoytrip.board.service;

import com.enjoytrip.board.dto.Board;
import java.util.List;

public interface BoardService {
    List<Board> getList(String type);
    Board getDetail(int boardId);
    int write(Board board);
    void modify(Board board, String loginUserId);
    void remove(int boardId, String loginUserId);
}
