package com.enjoytrip.member.controller;

import com.enjoytrip.common.exception.BusinessException;
import com.enjoytrip.member.dto.Member;
import com.enjoytrip.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "member/login";
    }

    @PostMapping("/login")
    public String login(String userId, String userPw, HttpSession session, Model model) {
        Member member = memberService.login(userId, userPw);
        if (member == null) {
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            return "member/login";
        }
        session.setAttribute("loginUser", member.getUserId());
        session.setAttribute("loginUserName", member.getUserName());
        return "redirect:/";
    }

    @GetMapping("/join")
    public String joinForm() {
        return "member/join";
    }

    @PostMapping("/join")
    public String join(Member member, Model model) {
        try {
            memberService.join(member);
            return "redirect:/user/login";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "member/join";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String mypage(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUser");
        model.addAttribute("member", memberService.getMemberById(userId));
        return "member/mypage";
    }

    @GetMapping("/modify")
    public String modifyForm(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUser");
        model.addAttribute("member", memberService.getMemberById(userId));
        return "member/modify";
    }

    @PostMapping("/modify")
    public String modify(Member member, HttpSession session) {
        String userId = (String) session.getAttribute("loginUser");
        member.setUserId(userId);
        memberService.modifyMember(member);
        session.setAttribute("loginUserName", member.getUserName());
        return "redirect:/user/mypage";
    }

    @PostMapping("/delete")
    public String delete(HttpSession session) {
        String userId = (String) session.getAttribute("loginUser");
        memberService.removeMember(userId);
        session.invalidate();
        return "redirect:/";
    }
}
