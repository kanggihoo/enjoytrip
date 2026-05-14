package com.enjoytrip.member.service;

import com.enjoytrip.member.dto.Member;

public interface MemberService {
    void join(Member member);
    Member login(String userId, String userPw);
    Member getMemberById(String userId);
    void modifyMember(Member member);
    void removeMember(String userId);
}
