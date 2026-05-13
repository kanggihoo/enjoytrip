package com.enjoytrip.board.controller;

import com.enjoytrip.board.dto.Board;
import com.enjoytrip.board.service.BoardService;
import com.enjoytrip.board.service.BoardServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/board/*")
public class BoardController extends HttpServlet {

    private final BoardService service = new BoardServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/list";
        switch (path) {
            case "/list":   showList(req, resp);   break;
            case "/write":  showWrite(req, resp);  break;
            case "/detail": showDetail(req, resp); break;
            case "/modify": showModify(req, resp); break;
            case "/delete": processDelete(req, resp); break;
            default: resp.sendRedirect(req.getContextPath() + "/board/list?type=free");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        if (path == null) path = "/";
        switch (path) {
            case "/regist": processWrite(req, resp);  break;
            case "/update": processModify(req, resp); break;
            default: resp.sendRedirect(req.getContextPath() + "/board/list?type=free");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String type = req.getParameter("type");
        if (type == null || (!type.equals("notice") && !type.equals("free"))) type = "free";
        try {
            List<Board> list = service.getList(type);
            req.setAttribute("boardList", list);
            req.setAttribute("type", type);
            req.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException(e); }
    }

    private void showWrite(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        String type = req.getParameter("type");
        if (type == null) type = "free";
        req.setAttribute("type", type);
        req.getRequestDispatcher("/WEB-INF/views/board/write.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("boardId");
        String type    = req.getParameter("type");
        if (type == null) type = "free";
        if (idParam == null) { resp.sendRedirect(req.getContextPath() + "/board/list?type=" + type); return; }
        try {
            Board board = service.getDetail(Integer.parseInt(idParam));
            req.setAttribute("board", board);
            req.setAttribute("type", type);
            req.getRequestDispatcher("/WEB-INF/views/board/detail.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException(e); }
    }

    private void showModify(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginUser = (String) req.getSession().getAttribute("loginUser");
        if (loginUser == null) { resp.sendRedirect(req.getContextPath() + "/user/login"); return; }
        String idParam = req.getParameter("boardId");
        String type    = req.getParameter("type");
        if (type == null) type = "free";
        try {
            Board board = service.getDetail(Integer.parseInt(idParam));
            if (!loginUser.equals(board.getUserId())) {
                resp.sendRedirect(req.getContextPath() + "/board/detail?boardId=" + idParam + "&type=" + type);
                return;
            }
            req.setAttribute("board", board);
            req.setAttribute("type", type);
            req.getRequestDispatcher("/WEB-INF/views/board/modify.jsp").forward(req, resp);
        } catch (Exception e) { throw new ServletException(e); }
    }

    private void processWrite(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginUser = (String) req.getSession().getAttribute("loginUser");
        if (loginUser == null) { resp.sendRedirect(req.getContextPath() + "/user/login"); return; }
        Board b = new Board();
        b.setType(req.getParameter("type"));
        b.setTitle(req.getParameter("title"));
        b.setContent(req.getParameter("content"));
        b.setUserId(loginUser);
        try {
            int id = service.write(b);
            resp.sendRedirect(req.getContextPath() + "/board/detail?boardId=" + id + "&type=" + b.getType());
        } catch (Exception e) { throw new ServletException(e); }
    }

    private void processModify(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginUser = (String) req.getSession().getAttribute("loginUser");
        if (loginUser == null) { resp.sendRedirect(req.getContextPath() + "/user/login"); return; }
        Board b = new Board();
        b.setBoardId(Integer.parseInt(req.getParameter("boardId")));
        b.setTitle(req.getParameter("title"));
        b.setContent(req.getParameter("content"));
        String type = req.getParameter("type");
        try {
            service.modify(b);
            resp.sendRedirect(req.getContextPath() + "/board/detail?boardId=" + b.getBoardId() + "&type=" + type);
        } catch (Exception e) { throw new ServletException(e); }
    }

    private void processDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginUser = (String) req.getSession().getAttribute("loginUser");
        if (loginUser == null) { resp.sendRedirect(req.getContextPath() + "/user/login"); return; }
        String type = req.getParameter("type");
        if (type == null) type = "free";
        try {
            service.remove(Integer.parseInt(req.getParameter("boardId")));
            resp.sendRedirect(req.getContextPath() + "/board/list?type=" + type);
        } catch (Exception e) { throw new ServletException(e); }
    }
}
