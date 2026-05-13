package com.enjoytrip.member.dao;

import com.enjoytrip.member.dto.Member;
import com.enjoytrip.util.DBUtil;

import java.sql.*;

public class MemberDaoImpl implements MemberDao {

    @Override
    public void insertMember(Member m) throws SQLException {
        String sql = "INSERT INTO member (user_id, user_pw, user_name, email) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getUserId());
            ps.setString(2, m.getUserPw());
            ps.setString(3, m.getUserName());
            ps.setString(4, m.getEmail());
            ps.executeUpdate();
        }
    }

    @Override
    public Member selectMemberById(String userId) throws SQLException {
        String sql = "SELECT * FROM member WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    @Override
    public Member selectMemberByIdAndPw(String userId, String userPw) throws SQLException {
        String sql = "SELECT * FROM member WHERE user_id = ? AND user_pw = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, userPw);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    @Override
    public void updateMember(Member m) throws SQLException {
        String sql = "UPDATE member SET user_pw = ?, user_name = ?, email = ? WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getUserPw());
            ps.setString(2, m.getUserName());
            ps.setString(3, m.getEmail());
            ps.setString(4, m.getUserId());
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteMember(String userId) throws SQLException {
        String sql = "DELETE FROM member WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsUserId(String userId) throws SQLException {
        String sql = "SELECT 1 FROM member WHERE user_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Member map(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setUserId(rs.getString("user_id"));
        m.setUserPw(rs.getString("user_pw"));
        m.setUserName(rs.getString("user_name"));
        m.setEmail(rs.getString("email"));
        m.setJoinDate(rs.getString("join_date"));
        return m;
    }
}
