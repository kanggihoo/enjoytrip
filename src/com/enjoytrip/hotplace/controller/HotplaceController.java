package com.enjoytrip.hotplace.controller;

import com.enjoytrip.hotplace.dto.Hotplace;
import com.enjoytrip.hotplace.service.HotplaceService;
import com.enjoytrip.hotplace.service.HotplaceServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * F105: 회원 주도의 Hotplace 등록 Controller
 *
 * URL 패턴:
 *   GET  /hotplace/list   - 전체 핫플레이스 목록
 *   GET  /hotplace/write  - 등록 폼
 *   POST /hotplace/regist - 등록 처리 (파일 업로드 포함)
 *   GET  /hotplace/detail - 상세 조회 (?hotplaceId=X)
 *   GET  /hotplace/modify - 수정 폼 (?hotplaceId=X)
 *   POST /hotplace/update - 수정 처리
 *   GET  /hotplace/delete - 삭제 처리 (?hotplaceId=X)
 */
@WebServlet("/hotplace/*")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1MB
    maxFileSize       = 1024 * 1024 * 10, // 10MB
    maxRequestSize    = 1024 * 1024 * 15  // 15MB
)
public class HotplaceController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final HotplaceService service = new HotplaceServiceImpl();
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String action = getAction(req);
        try {
            switch (action) {
                case "/list":   list(req, resp);       break;
                case "/write":  writeForm(req, resp);  break;
                case "/detail": detail(req, resp);     break;
                case "/modify": modifyForm(req, resp); break;
                case "/delete": delete(req, resp);     break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/hotplace/list");
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
                case "/regist": regist(req, resp); break;
                case "/update": update(req, resp); break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/hotplace/list");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // ------------------------------------------------------------------
    // 전체 목록
    // ------------------------------------------------------------------
    private void list(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        List<Hotplace> hotplaces = service.getAllHotplaces();
        req.setAttribute("hotplaces", hotplaces);
        req.getRequestDispatcher("/WEB-INF/views/hotplace/list.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // 등록 폼
    // ------------------------------------------------------------------
    private void writeForm(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        if (!isLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/hotplace/write.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // 등록 처리
    // ------------------------------------------------------------------
    private void regist(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        HttpSession session = req.getSession(false);
        if (!isLoggedIn(session)) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }

        String imagePath = saveUploadedFile(req);

        Hotplace hotplace = buildHotplaceFromRequest(req);
        hotplace.setUserId((String) session.getAttribute("loginUser"));
        hotplace.setImagePath(imagePath);
        service.registerHotplace(hotplace);
        resp.sendRedirect(req.getContextPath() + "/hotplace/list");
    }

    // ------------------------------------------------------------------
    // 상세 조회
    // ------------------------------------------------------------------
    private void detail(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        int hotplaceId = Integer.parseInt(req.getParameter("hotplaceId"));
        Hotplace hotplace = service.getHotplaceById(hotplaceId);
        req.setAttribute("hotplace", hotplace);
        req.getRequestDispatcher("/WEB-INF/views/hotplace/detail.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // 수정 폼
    // ------------------------------------------------------------------
    private void modifyForm(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        if (!isLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        int hotplaceId = Integer.parseInt(req.getParameter("hotplaceId"));
        Hotplace hotplace = service.getHotplaceById(hotplaceId);
        req.setAttribute("hotplace", hotplace);
        req.getRequestDispatcher("/WEB-INF/views/hotplace/modify.jsp").forward(req, resp);
    }

    // ------------------------------------------------------------------
    // 수정 처리
    // ------------------------------------------------------------------
    private void update(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        if (!isLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        int hotplaceId = Integer.parseInt(req.getParameter("hotplaceId"));
        Hotplace hotplace = buildHotplaceFromRequest(req);
        hotplace.setHotplaceId(hotplaceId);

        // 새 이미지가 업로드된 경우에만 교체
        String newImagePath = saveUploadedFile(req);
        if (newImagePath != null && !newImagePath.isEmpty()) {
            hotplace.setImagePath(newImagePath);
        } else {
            hotplace.setImagePath(req.getParameter("existingImage"));
        }
        service.modifyHotplace(hotplace);
        resp.sendRedirect(req.getContextPath() + "/hotplace/detail?hotplaceId=" + hotplaceId);
    }

    // ------------------------------------------------------------------
    // 삭제
    // ------------------------------------------------------------------
    private void delete(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        if (!isLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        int hotplaceId = Integer.parseInt(req.getParameter("hotplaceId"));
        service.removeHotplace(hotplaceId);
        resp.sendRedirect(req.getContextPath() + "/hotplace/list");
    }

    // ------------------------------------------------------------------
    // 파일 업로드 처리
    // ------------------------------------------------------------------
    private String saveUploadedFile(HttpServletRequest req) throws Exception {
        Part filePart = req.getPart("image");
        if (filePart == null || filePart.getSize() == 0) return "";

        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String originalName = getFileName(filePart);
        if (originalName == null || originalName.isEmpty()) return "";

        String ext = originalName.substring(originalName.lastIndexOf('.'));
        String savedName = UUID.randomUUID().toString() + ext;
        filePart.write(uploadPath + File.separator + savedName);

        return UPLOAD_DIR + "/" + savedName;
    }

    private String getFileName(Part part) {
        String header = part.getHeader("content-disposition");
        if (header == null) return null;
        for (String token : header.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
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

    private Hotplace buildHotplaceFromRequest(HttpServletRequest req) throws Exception {
        Hotplace h = new Hotplace();
        h.setTitle(req.getParameter("title"));
        h.setVisitDate(req.getParameter("visitDate"));
        h.setPlaceType(req.getParameter("placeType"));
        h.setDescription(req.getParameter("description"));
        try { h.setLatitude(Double.parseDouble(req.getParameter("latitude"))); }
        catch (Exception e) { h.setLatitude(0.0); }
        try { h.setLongitude(Double.parseDouble(req.getParameter("longitude"))); }
        catch (Exception e) { h.setLongitude(0.0); }
        return h;
    }
}
