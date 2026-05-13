package com.enjoytrip.plan.controller;

import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;
import com.enjoytrip.plan.service.TravelPlanService;
import com.enjoytrip.plan.service.TravelPlanServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * F104: 여행 계획 경로 설정 Controller
 *
 * URL 패턴:
 *   GET  /plan/list       - 내 여행계획 목록
 *   GET  /plan/write      - 계획 등록 폼
 *   POST /plan/regist     - 계획 등록 처리
 *   GET  /plan/detail     - 계획 상세 조회 (?planId=X)
 *   GET  /plan/modify     - 계획 수정 폼 (?planId=X)
 *   POST /plan/update     - 계획 수정 처리
 *   GET  /plan/delete     - 계획 삭제 처리 (?planId=X)
 *   POST /plan/addDetail  - 방문지 추가
 *   POST /plan/updateOrder- 방문지 순서 변경
 */
@WebServlet("/plan/*")
public class PlanController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final TravelPlanService service = new TravelPlanServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = getAction(req);
        try {
            switch (action) {
                case "/list":    list(req, resp);   break;
                case "/write":   writeForm(req, resp); break;
                case "/detail":  detail(req, resp); break;
                case "/modify":  modifyForm(req, resp); break;
                case "/delete":  delete(req, resp); break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/plan/list");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = getAction(req);
        try {
            switch (action) {
                case "/regist":      regist(req, resp);      break;
                case "/update":      update(req, resp);      break;
                case "/addDetail":   addDetail(req, resp);   break;
                case "/updateOrder": updateOrder(req, resp); break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/plan/list");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // ------------------------------------------------------------------
    // 내 여행계획 목록
    // ------------------------------------------------------------------
    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        HttpSession session = req.getSession(false);
        if (!isLoggedIn(session)) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        String userId = (String) session.getAttribute("loginUser");
        List<TravelPlan> plans = service.getPlansByUserId(userId);
        req.setAttribute("plans", plans);
        req.getRequestDispatcher("/WEB-INF/views/plan/list.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // 계획 등록 폼
    // ------------------------------------------------------------------
    private void writeForm(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        if (!isLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/plan/write.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // 계획 등록 처리 (방문지 JSON 포함)
    // ------------------------------------------------------------------
    private void regist(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        HttpSession session = req.getSession(false);
        if (!isLoggedIn(session)) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        TravelPlan plan = buildPlanFromRequest(req);
        plan.setUserId((String) session.getAttribute("loginUser"));
        int newPlanId = service.createPlan(plan);

        // write.jsp 에서 전송된 방문지 JSON 파싱 후 일괄 저장
        String detailsJson = req.getParameter("detailsJson");
        if (detailsJson != null && !detailsJson.trim().isEmpty()
                && !detailsJson.trim().equals("[]")) {
            List<PlanDetail> details = parseDetailsJson(detailsJson);
            if (!details.isEmpty()) {
                service.replaceDetails(newPlanId, details);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/plan/detail?planId=" + newPlanId);
    }

    // ------------------------------------------------------------------
    // 계획 상세 조회
    // ------------------------------------------------------------------
    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int planId = Integer.parseInt(req.getParameter("planId"));
        TravelPlan plan = service.getPlanById(planId);
        req.setAttribute("plan", plan);
        req.getRequestDispatcher("/WEB-INF/views/plan/detail.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // 계획 수정 폼
    // ------------------------------------------------------------------
    private void modifyForm(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        HttpSession session = req.getSession(false);
        if (!isLoggedIn(session)) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        int planId = Integer.parseInt(req.getParameter("planId"));
        TravelPlan plan = service.getPlanById(planId);
        req.setAttribute("plan", plan);
        req.getRequestDispatcher("/WEB-INF/views/plan/modify.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // 계획 수정 처리
    // ------------------------------------------------------------------
    private void update(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        if (!isLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        TravelPlan plan = buildPlanFromRequest(req);
        plan.setPlanId(Integer.parseInt(req.getParameter("planId")));
        service.modifyPlan(plan);
        resp.sendRedirect(req.getContextPath() + "/plan/detail?planId=" + plan.getPlanId());
    }

    // ------------------------------------------------------------------
    // 계획 삭제
    // ------------------------------------------------------------------
    private void delete(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        if (!isLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        int planId = Integer.parseInt(req.getParameter("planId"));
        service.removePlan(planId);
        resp.sendRedirect(req.getContextPath() + "/plan/list");
    }

    // ------------------------------------------------------------------
    // 방문지(관광지) 추가
    // ------------------------------------------------------------------
    private void addDetail(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int planId = Integer.parseInt(req.getParameter("planId"));
        PlanDetail detail = new PlanDetail();
        detail.setPlanId(planId);
        detail.setContentId(parseIntSafe(req.getParameter("contentId")));
        detail.setTitle(req.getParameter("title"));
        detail.setLatitude(parseDoubleSafe(req.getParameter("latitude")));
        detail.setLongitude(parseDoubleSafe(req.getParameter("longitude")));
        detail.setVisitDate(req.getParameter("visitDate"));
        detail.setMemo(req.getParameter("memo"));

        // 현재 마지막 순서 다음에 추가
        List<PlanDetail> existing = service.getDetailsByPlanId(planId);
        detail.setVisitOrder(existing.size() + 1);

        service.addDetail(detail);
        resp.sendRedirect(req.getContextPath() + "/plan/detail?planId=" + planId);
    }

    // ------------------------------------------------------------------
    // 드래그앤드롭 순서 변경 (AJAX 호출 가정)
    // ------------------------------------------------------------------
    private void updateOrder(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int detailId   = Integer.parseInt(req.getParameter("detailId"));
        int visitOrder = Integer.parseInt(req.getParameter("visitOrder"));
        service.changeDetailOrder(detailId, visitOrder);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write("{\"result\":\"ok\"}");
    }

    // ------------------------------------------------------------------
    // 공통 유틸
    // ------------------------------------------------------------------
    private String getAction(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        return (pathInfo == null || pathInfo.isEmpty()) ? "/" : pathInfo;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("loginUser") != null;
    }

    private TravelPlan buildPlanFromRequest(HttpServletRequest req) {
        TravelPlan plan = new TravelPlan();
        plan.setTitle(req.getParameter("title"));
        plan.setStartDate(req.getParameter("startDate"));
        plan.setEndDate(req.getParameter("endDate"));
        plan.setTotalBudget(parseIntSafe(req.getParameter("totalBudget")));
        plan.setMemo(req.getParameter("memo"));
        return plan;
    }

    private int parseIntSafe(String val) {
        try { return Integer.parseInt(val); } catch (Exception e) { return 0; }
    }

    private double parseDoubleSafe(String val) {
        try { return Double.parseDouble(val); } catch (Exception e) { return 0.0; }
    }

    /**
     * write.jsp 가 전송하는 JSON 배열을 외부 라이브러리 없이 파싱한다.
     * 형식: [{"title":"...","latitude":"37.5","longitude":"127.0","visitOrder":1}, ...]
     */
    private List<PlanDetail> parseDetailsJson(String json) {
        List<PlanDetail> list = new ArrayList<>();
        if (json == null) return list;

        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]"))   json = json.substring(0, json.length() - 1);
        json = json.trim();
        if (json.isEmpty()) return list;

        // 각 객체 분리: },{  를 기준으로 split 후 중괄호 제거
        String[] objects = json.split("\\},\\s*\\{");
        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "").trim();
            PlanDetail d = new PlanDetail();
            for (String token : obj.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
                String[] kv = token.split(":", 2);
                if (kv.length < 2) continue;
                String key = kv[0].trim().replace("\"", "");
                String val = kv[1].trim().replace("\"", "");
                switch (key) {
                    case "title":      d.setTitle(val);                     break;
                    case "latitude":   d.setLatitude(parseDoubleSafe(val)); break;
                    case "longitude":  d.setLongitude(parseDoubleSafe(val));break;
                    case "visitOrder": d.setVisitOrder(parseIntSafe(val));  break;
                }
            }
            list.add(d);
        }
        return list;
    }
}
