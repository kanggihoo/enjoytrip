package com.enjoytrip.plan.dao;

import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;
import com.enjoytrip.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TravelPlanDaoImpl implements TravelPlanDao {

    @Override
    public int insertPlan(TravelPlan plan) throws SQLException {
        String sql = "INSERT INTO travel_plan (user_id, title, start_date, end_date, total_budget, memo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int generatedId = 0;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, plan.getUserId());
            pstmt.setString(2, plan.getTitle());
            pstmt.setString(3, plan.getStartDate());
            pstmt.setString(4, plan.getEndDate());
            pstmt.setInt(5, plan.getTotalBudget());
            pstmt.setString(6, plan.getMemo());
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) generatedId = rs.getInt(1);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return generatedId;
    }

    @Override
    public TravelPlan selectPlanById(int planId) throws SQLException {
        String sql = "SELECT * FROM travel_plan WHERE plan_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        TravelPlan plan = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planId);
            rs = pstmt.executeQuery();
            if (rs.next()) plan = mapRow(rs);
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return plan;
    }

    @Override
    public List<TravelPlan> selectPlansByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM travel_plan WHERE user_id = ? ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TravelPlan> list = new ArrayList<>();
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
    public void updatePlan(TravelPlan plan) throws SQLException {
        String sql = "UPDATE travel_plan SET title=?, start_date=?, end_date=?, " +
                     "total_budget=?, memo=? WHERE plan_id=?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, plan.getTitle());
            pstmt.setString(2, plan.getStartDate());
            pstmt.setString(3, plan.getEndDate());
            pstmt.setInt(4, plan.getTotalBudget());
            pstmt.setString(5, plan.getMemo());
            pstmt.setInt(6, plan.getPlanId());
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    @Override
    public void deletePlan(int planId) throws SQLException {
        String sql = "DELETE FROM travel_plan WHERE plan_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planId);
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    @Override
    public void insertDetail(PlanDetail detail) throws SQLException {
        String sql = "INSERT INTO plan_detail (plan_id, content_id, title, latitude, longitude, " +
                     "visit_order, visit_date, memo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, detail.getPlanId());
            pstmt.setInt(2, detail.getContentId());
            pstmt.setString(3, detail.getTitle());
            pstmt.setDouble(4, detail.getLatitude());
            pstmt.setDouble(5, detail.getLongitude());
            pstmt.setInt(6, detail.getVisitOrder());
            pstmt.setString(7, detail.getVisitDate());
            pstmt.setString(8, detail.getMemo());
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    @Override
    public List<PlanDetail> selectDetailsByPlanId(int planId) throws SQLException {
        String sql = "SELECT * FROM plan_detail WHERE plan_id = ? ORDER BY visit_order ASC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<PlanDetail> list = new ArrayList<>();
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planId);
            rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapDetailRow(rs));
        } finally {
            DBUtil.close(rs, pstmt, conn);
        }
        return list;
    }

    @Override
    public void deleteDetailsByPlanId(int planId) throws SQLException {
        String sql = "DELETE FROM plan_detail WHERE plan_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, planId);
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    @Override
    public void updateDetailOrder(int detailId, int visitOrder) throws SQLException {
        String sql = "UPDATE plan_detail SET visit_order = ? WHERE detail_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn  = DBUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, visitOrder);
            pstmt.setInt(2, detailId);
            pstmt.executeUpdate();
        } finally {
            DBUtil.close(pstmt, conn);
        }
    }

    private TravelPlan mapRow(ResultSet rs) throws SQLException {
        TravelPlan plan = new TravelPlan();
        plan.setPlanId(rs.getInt("plan_id"));
        plan.setUserId(rs.getString("user_id"));
        plan.setTitle(rs.getString("title"));
        plan.setStartDate(rs.getString("start_date"));
        plan.setEndDate(rs.getString("end_date"));
        plan.setTotalBudget(rs.getInt("total_budget"));
        plan.setMemo(rs.getString("memo"));
        plan.setCreatedAt(rs.getString("created_at"));
        plan.setUpdatedAt(rs.getString("updated_at"));
        return plan;
    }

    private PlanDetail mapDetailRow(ResultSet rs) throws SQLException {
        PlanDetail d = new PlanDetail();
        d.setDetailId(rs.getInt("detail_id"));
        d.setPlanId(rs.getInt("plan_id"));
        d.setContentId(rs.getInt("content_id"));
        d.setTitle(rs.getString("title"));
        d.setLatitude(rs.getDouble("latitude"));
        d.setLongitude(rs.getDouble("longitude"));
        d.setVisitOrder(rs.getInt("visit_order"));
        d.setVisitDate(rs.getString("visit_date"));
        d.setMemo(rs.getString("memo"));
        return d;
    }
}
