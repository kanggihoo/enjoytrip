package com.enjoytrip.attraction.controller;

import com.enjoytrip.attraction.dto.Sido;
import com.enjoytrip.attraction.service.AttractionService;
import com.enjoytrip.attraction.service.AttractionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@WebServlet("/attraction/*")
public class AttractionController extends HttpServlet {

    // 공공데이터포털 서비스키 (URL 인코딩 필요: + → %2B, = → %3D)
    private static final String SERVICE_KEY = "esd4FqTqzukG142FwyrZNUQxyRcuVzzToQEqC9PfVcilytDCYDXoMLnCA2zOQzEbZEC05M9F%2B1nXwyC8I2KohQ%3D%3D";
    private static final String API_BASE = "https://apis.data.go.kr/B551011/KorService2";

    private final AttractionService service = new AttractionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null)
            path = "/list";

        switch (path) {
            case "/list":
                showList(req, resp);
                break;
            case "/detail":
                showDetail(req, resp);
                break;
            // 공공 API 프록시 엔드포인트 (CORS 우회)
            case "/api/sido":
                proxySido(req, resp);
                break;
            case "/api/gugun":
                proxyGugun(req, resp);
                break;
            case "/api/search":
                proxySearch(req, resp);
                break;
            case "/api/detail":
                proxyDetail(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/attraction/list");
        }
    }

    /*
     * ======================================================
     * 페이지 렌더링
     * ======================================================
     */

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            List<Sido> sidoList = service.getAllSido();
            req.setAttribute("sidoList", sidoList);
        } catch (Exception ignored) {
            req.setAttribute("sidoList", Collections.emptyList());
        }
        req.getRequestDispatcher("/WEB-INF/views/attraction/list.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("contentId", req.getParameter("contentId"));
        req.getRequestDispatcher("/WEB-INF/views/attraction/detail.jsp").forward(req, resp);
    }

    /*
     * ======================================================
     * 공공 API 프록시 (브라우저 CORS 우회용)
     * ======================================================
     */

    private void proxySido(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = API_BASE + "/areaCode2"
                + "?serviceKey=" + SERVICE_KEY
                + "&numOfRows=50&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json";
        forwardApiResponse(url, resp);
    }

    private void proxyGugun(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sidoCode = req.getParameter("sidoCode");
        String url = API_BASE + "/areaCode2"
                + "?serviceKey=" + SERVICE_KEY
                + "&numOfRows=100&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json"
                + (sidoCode != null ? "&areaCode=" + enc(sidoCode) : "");
        forwardApiResponse(url, resp);
    }

    private void proxySearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        StringBuilder url = new StringBuilder(API_BASE + "/areaBasedList2"
                + "?serviceKey=" + SERVICE_KEY
                + "&numOfRows=100&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json");
        append(url, "areaCode", req.getParameter("areaCode"));
        append(url, "sigunguCode", req.getParameter("sigunguCode"));
        append(url, "contentTypeId", req.getParameter("contentTypeId"));
        append(url, "keyword", req.getParameter("keyword"));
        forwardApiResponse(url.toString(), resp);
    }

    private void proxyDetail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contentId = req.getParameter("contentId");
        String url = API_BASE + "/detailCommon2"
                + "?serviceKey=" + SERVICE_KEY
                + "&numOfRows=1&pageNo=1&MobileOS=ETC&MobileApp=AppTest&_type=json"
                + (contentId != null ? "&contentId=" + enc(contentId) : "");
        forwardApiResponse(url, resp);
    }

    /*
     * ======================================================
     * HTTP 호출 후 응답을 그대로 클라이언트에 전달
     * ======================================================
     */

    private void forwardApiResponse(String apiUrl, HttpServletResponse resp) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(apiUrl).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10_000);
            conn.setReadTimeout(15_000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int code = conn.getResponseCode();
            resp.setContentType("application/json;charset=UTF-8");
            resp.setStatus(code);

            InputStream is = (code < 400) ? conn.getInputStream() : conn.getErrorStream();
            try (OutputStream os = resp.getOutputStream()) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) != -1)
                    os.write(buf, 0, n);
            }
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    private void append(StringBuilder sb, String key, String value) throws IOException {
        if (value != null && !value.isEmpty())
            sb.append("&").append(key).append("=").append(enc(value));
    }

    private String enc(String v) throws IOException {
        return URLEncoder.encode(v, StandardCharsets.UTF_8.name());
    }
}
