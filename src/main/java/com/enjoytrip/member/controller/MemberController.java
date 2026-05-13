package com.enjoytrip.member.controller;

import com.enjoytrip.member.dto.Member;
import com.enjoytrip.member.service.MemberService;
import com.enjoytrip.member.service.MemberServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/user/*")
public class MemberController extends HttpServlet {

    private final MemberService service = new MemberServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/login":  showLogin(req, resp);  break;
            case "/join":   showJoin(req, resp);   break;
            case "/logout": logout(req, resp);     break;
            case "/mypage": showMypage(req, resp); break;
            case "/modify": showModify(req, resp); break;
            default: resp.sendRedirect(req.getContextPath() + "/");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        if (path == null) path = "/";

        switch (path) {
            case "/login":  processLogin(req, resp);  break;
            case "/join":   processJoin(req, resp);   break;
            case "/modify": processModify(req, resp); break;
            case "/delete": processDelete(req, resp); break;
            default: resp.sendRedirect(req.getContextPath() + "/");
        }
    }

    private void showLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/member/login.jsp").forward(req, resp);
    }

    private void showJoin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/member/join.jsp").forward(req, resp);
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        resp.sendRedirect(req.getContextPath() + "/");
    }

    private void showMypage(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginUser = (String) req.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        try {
            Member member = service.getMemberById(loginUser);
            req.setAttribute("member", member);
            req.getRequestDispatcher("/WEB-INF/views/member/mypage.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void showModify(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginUser = (String) req.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        try {
            Member member = service.getMemberById(loginUser);
            req.setAttribute("member", member);
            req.getRequestDispatcher("/WEB-INF/views/member/modify.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void processLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String userPw = req.getParameter("userPw");
        try {
            Member member = service.login(userId, userPw);
            if (member == null) {
                req.setAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
                req.getRequestDispatcher("/WEB-INF/views/member/login.jsp").forward(req, resp);
                return;
            }
            HttpSession session = req.getSession();
            session.setAttribute("loginUser", member.getUserId());
            session.setAttribute("loginUserName", member.getUserName());
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void processJoin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Member member = new Member();
        member.setUserId(req.getParameter("userId"));
        member.setUserPw(req.getParameter("userPw"));
        member.setUserName(req.getParameter("userName"));
        member.setEmail(req.getParameter("email"));
        try {
            service.join(member);
            resp.sendRedirect(req.getContextPath() + "/user/login");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/member/join.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void processModify(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginUser = (String) req.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        Member member = new Member();
        member.setUserId(loginUser);
        member.setUserPw(req.getParameter("userPw"));
        member.setUserName(req.getParameter("userName"));
        member.setEmail(req.getParameter("email"));
        try {
            service.modifyMember(member);
            req.getSession().setAttribute("loginUserName", member.getUserName());
            resp.sendRedirect(req.getContextPath() + "/user/mypage");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void processDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String loginUser = (String) req.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            resp.sendRedirect(req.getContextPath() + "/user/login");
            return;
        }
        try {
            service.removeMember(loginUser);
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
