package com.enjoytrip.board.dao;

import com.enjoytrip.board.dto.Board;
import com.enjoytrip.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BoardDaoImpl implements BoardDao {

    @Override
    public List<Board> selectByType(String type) throws SQLException {
        String sql = "SELECT * FROM board WHERE type = ? ORDER BY created_at DESC";
        List<Board> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public Board selectById(int boardId) throws SQLException {
        String sql = "SELECT * FROM board WHERE board_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, boardId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    @Override
    public int insert(Board b) throws SQLException {
        String sql = "INSERT INTO board (type, title, content, user_id) VALUES (?,?,?,?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, b.getType());
            ps.setString(2, b.getTitle());
            ps.setString(3, b.getContent());
            ps.setString(4, b.getUserId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public void update(Board b) throws SQLException {
        String sql = "UPDATE board SET title=?, content=? WHERE board_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getContent());
            ps.setInt(3, b.getBoardId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int boardId) throws SQLException {
        String sql = "DELETE FROM board WHERE board_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, boardId);
            ps.executeUpdate();
        }
    }

    @Override
    public void incrementViews(int boardId) throws SQLException {
        String sql = "UPDATE board SET views = views + 1 WHERE board_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, boardId);
            ps.executeUpdate();
        }
    }

    private Board map(ResultSet rs) throws SQLException {
        Board b = new Board();
        b.setBoardId(rs.getInt("board_id"));
        b.setType(rs.getString("type"));
        b.setTitle(rs.getString("title"));
        b.setContent(rs.getString("content"));
        b.setUserId(rs.getString("user_id"));
        b.setViews(rs.getInt("views"));
        b.setCreatedAt(rs.getString("created_at"));
        b.setUpdatedAt(rs.getString("updated_at"));
        return b;
    }
}
