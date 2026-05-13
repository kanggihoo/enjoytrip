package com.enjoytrip.hotplace.dao;

import com.enjoytrip.hotplace.dto.Hotplace;
import com.enjoytrip.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HotplaceDaoImpl implements HotplaceDao {

    @Override
    public void insertHotplace(Hotplace hotplace) throws SQLException {
        String sql = "INSERT INTO hotplace (user_id, title, visit_date, place_type, " +
                     "description, latitude, longitude, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hotplace.getUserId());
            pstmt.setString(2, hotplace.getTitle());
            pstmt.setString(3, hotplace.getVisitDate());
            pstmt.setString(4, hotplace.getPlaceType());
            pstmt.setString(5, hotplace.getDescription());
            pstmt.setDouble(6, hotplace.getLatitude());
            pstmt.setDouble(7, hotplace.getLongitude());
            pstmt.setString(8, hotplace.getImagePath());
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    @Override
    public Hotplace selectHotplaceById(int hotplaceId) throws SQLException {
        String sql = "SELECT * FROM hotplace WHERE hotplace_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Hotplace hotplace = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hotplaceId);
            rs = pstmt.executeQuery();
            if (rs.next()) hotplace = mapRow(rs);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return hotplace;
    }

    @Override
    public List<Hotplace> selectAllHotplaces() throws SQLException {
        String sql = "SELECT * FROM hotplace ORDER BY created_at DESC";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Hotplace> list = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            stmt = conn.createStatement();
            rs   = stmt.executeQuery(sql);
            while (rs.next()) list.add(mapRow(rs));
        } finally {
            DBUtil.close(rs, stmt, conn);
        }
        return list;
    }

    @Override
    public List<Hotplace> selectHotplacesByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM hotplace WHERE user_id = ? ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Hotplace> list = new ArrayList<>();
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return list;
    }

    @Override
    public void updateHotplace(Hotplace hotplace) throws SQLException {
        String sql = "UPDATE hotplace SET title=?, visit_date=?, place_type=?, " +
                     "description=?, latitude=?, longitude=?, image_path=? WHERE hotplace_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hotplace.getTitle());
            pstmt.setString(2, hotplace.getVisitDate());
            pstmt.setString(3, hotplace.getPlaceType());
            pstmt.setString(4, hotplace.getDescription());
            pstmt.setDouble(5, hotplace.getLatitude());
            pstmt.setDouble(6, hotplace.getLongitude());
            pstmt.setString(7, hotplace.getImagePath());
            pstmt.setInt(8, hotplace.getHotplaceId());
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    @Override
    public void deleteHotplace(int hotplaceId) throws SQLException {
        String sql = "DELETE FROM hotplace WHERE hotplace_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, hotplaceId);
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    private Hotplace mapRow(ResultSet rs) throws SQLException {
        Hotplace h = new Hotplace();
        h.setHotplaceId(rs.getInt("hotplace_id"));
        h.setUserId(rs.getString("user_id"));
        h.setTitle(rs.getString("title"));
        h.setVisitDate(rs.getString("visit_date"));
        h.setPlaceType(rs.getString("place_type"));
        h.setDescription(rs.getString("description"));
        h.setLatitude(rs.getDouble("latitude"));
        h.setLongitude(rs.getDouble("longitude"));
        h.setImagePath(rs.getString("image_path"));
        h.setCreatedAt(rs.getString("created_at"));
        return h;
    }
}
