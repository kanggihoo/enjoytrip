package com.enjoytrip.board.service;

import com.enjoytrip.board.dao.BoardDao;
import com.enjoytrip.board.dao.BoardDaoImpl;
import com.enjoytrip.board.dto.Board;

import java.sql.SQLException;
import java.util.List;

public class BoardServiceImpl implements BoardService {

    private final BoardDao dao = new BoardDaoImpl();

    @Override
    public List<Board> getList(String type) throws SQLException {
        return dao.selectByType(type);
    }

    @Override
    public Board getDetail(int boardId) throws SQLException {
        dao.incrementViews(boardId);
        return dao.selectById(boardId);
    }

    @Override
    public int write(Board board) throws SQLException {
        return dao.insert(board);
    }

    @Override
    public void modify(Board board) throws SQLException {
        dao.update(board);
    }

    @Override
    public void remove(int boardId) throws SQLException {
        dao.delete(boardId);
    }
}
