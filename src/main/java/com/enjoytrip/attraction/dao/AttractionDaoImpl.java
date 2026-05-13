package com.enjoytrip.attraction.dao;

import com.enjoytrip.attraction.dto.AttractionInfo;
import com.enjoytrip.attraction.dto.Gugun;
import com.enjoytrip.attraction.dto.Sido;
import com.enjoytrip.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttractionDaoImpl implements AttractionDao {

    @Override
    public List<Sido> selectAllSido() throws SQLException {
        List<Sido> list = new ArrayList<>();
        String sql = "SELECT sido_code, sido_name FROM sido ORDER BY sido_code";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Sido(rs.getInt("sido_code"), rs.getString("sido_name")));
            }
        }
        return list;
    }

    @Override
    public List<Gugun> selectGugunBySido(int sidoCode) throws SQLException {
        List<Gugun> list = new ArrayList<>();
        String sql = "SELECT gugun_code, sido_code, gugun_name FROM gugun WHERE sido_code = ? ORDER BY gugun_code";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sidoCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Gugun(rs.getInt("gugun_code"), rs.getInt("sido_code"), rs.getString("gugun_name")));
                }
            }
        }
        return list;
    }

    @Override
    public List<AttractionInfo> selectAttractions(int sidoCode, int gugunCode, int contentTypeId, String keyword) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM attraction_info WHERE 1=1");
        if (sidoCode > 0)      sql.append(" AND sido_code = ?");
        if (gugunCode > 0)     sql.append(" AND gugun_code = ?");
        if (contentTypeId > 0) sql.append(" AND content_type_id = ?");
        if (keyword != null && !keyword.trim().isEmpty()) sql.append(" AND title LIKE ?");
        sql.append(" ORDER BY readcount DESC LIMIT 100");

        List<AttractionInfo> list = new ArrayList<>();
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int idx = 1;
            if (sidoCode > 0)      ps.setInt(idx++, sidoCode);
            if (gugunCode > 0)     ps.setInt(idx++, gugunCode);
            if (contentTypeId > 0) ps.setInt(idx++, contentTypeId);
            if (keyword != null && !keyword.trim().isEmpty()) ps.setString(idx++, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapInfo(rs));
            }
        }
        return list;
    }

    @Override
    public AttractionInfo selectAttractionById(int contentId) throws SQLException {
        String sql = "SELECT * FROM attraction_info WHERE content_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapInfo(rs);
            }
        }
        return null;
    }

    @Override
    public void incrementReadcount(int contentId) throws SQLException {
        String sql = "UPDATE attraction_info SET readcount = readcount + 1 WHERE content_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contentId);
            ps.executeUpdate();
        }
    }

    private AttractionInfo mapInfo(ResultSet rs) throws SQLException {
        AttractionInfo a = new AttractionInfo();
        a.setContentId(rs.getInt("content_id"));
        a.setContentTypeId(rs.getInt("content_type_id"));
        a.setTitle(rs.getString("title"));
        a.setSidoCode(rs.getInt("sido_code"));
        a.setGugunCode(rs.getInt("gugun_code"));
        a.setAddr1(rs.getString("addr1"));
        a.setAddr2(rs.getString("addr2"));
        a.setZipcode(rs.getString("zipcode"));
        a.setTel(rs.getString("tel"));
        a.setFirstImage(rs.getString("first_image"));
        a.setFirstImage2(rs.getString("first_image2"));
        a.setReadcount(rs.getInt("readcount"));
        a.setMapx(rs.getDouble("mapx"));
        a.setMapy(rs.getDouble("mapy"));
        a.setMlevel(rs.getInt("mlevel"));
        a.setCat1(rs.getString("cat1"));
        a.setCat2(rs.getString("cat2"));
        a.setCat3(rs.getString("cat3"));
        a.setOverview(rs.getString("overview"));
        return a;
    }
}
