package com.enjoytrip.member.controller;

import com.enjoytrip.member.dto.Member;
import com.enjoytrip.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Test
    void loginFormReturnsLoginView() throws Exception {
        mockMvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/login"));
    }

    @Test
    void loginStoresSessionAndRedirectsHome() throws Exception {
        when(memberService.login("ssafy", "ssafy"))
                .thenReturn(Member.builder().userId("ssafy").userName("관리자").build());

        mockMvc.perform(post("/user/login")
                        .param("userId", "ssafy")
                        .param("userPw", "ssafy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(request().sessionAttribute("loginUser", "ssafy"))
                .andExpect(request().sessionAttribute("loginUserName", "관리자"));
    }

    @Test
    void loginReturnsLoginViewWhenCredentialsDoNotMatch() throws Exception {
        when(memberService.login("ssafy", "bad")).thenReturn(null);

        mockMvc.perform(post("/user/login")
                        .param("userId", "ssafy")
                        .param("userPw", "bad"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/login"))
                .andExpect(model().attribute("error", "아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void joinBindsMemberAndRedirectsLogin() throws Exception {
        mockMvc.perform(post("/user/join")
                        .param("userId", "new-user")
                        .param("userPw", "pw")
                        .param("userName", "새회원")
                        .param("email", "new@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));

        verify(memberService).join(any(Member.class));
    }

    @Test
    void modifyUsesLoggedInUserId() throws Exception {
        mockMvc.perform(post("/user/modify")
                        .sessionAttr("loginUser", "ssafy")
                        .param("userPw", "new-pw")
                        .param("userName", "변경")
                        .param("email", "updated@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/mypage"))
                .andExpect(request().sessionAttribute("loginUserName", "변경"));

        verify(memberService).modifyMember(any(Member.class));
    }
}
