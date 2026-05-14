package com.enjoytrip.board.service;

import com.enjoytrip.board.dto.Board;
import com.enjoytrip.board.mapper.BoardMapper;
import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.ForbiddenException;
import com.enjoytrip.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

    @Override
    public List<Board> getList(String type) {
        return boardMapper.selectBoardsByType(type);
    }

    @Override
    @Transactional
    public Board getDetail(int boardId) {
        Board board = requireBoard(boardId);
        boardMapper.increaseViews(boardId);
        board.setViews(board.getViews() + 1);
        return board;
    }

    @Override
    public int write(Board board) {
        boardMapper.insertBoard(board);
        return board.getBoardId();
    }

    @Override
    public void modify(Board board, String loginUserId) {
        Board saved = requireBoard(board.getBoardId());
        requireOwner(saved, loginUserId);
        boardMapper.updateBoard(board);
    }

    @Override
    public void remove(int boardId, String loginUserId) {
        Board board = requireBoard(boardId);
        requireOwner(board, loginUserId);
        boardMapper.deleteBoard(boardId);
    }

    private Board requireBoard(int boardId) {
        Board board = boardMapper.selectBoardById(boardId);
        if (board == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND);
        }
        return board;
    }

    private void requireOwner(Board board, String loginUserId) {
        if (!board.getUserId().equals(loginUserId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
    }
}
