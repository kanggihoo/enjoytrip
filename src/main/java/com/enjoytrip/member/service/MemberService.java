package com.enjoytrip.member.service;

import com.enjoytrip.member.dto.Member;
import java.sql.SQLException;

public interface MemberService {
    void join(Member member) throws SQLException, IllegalArgumentException;
    Member login(String userId, String userPw) throws SQLException;
    Member getMemberById(String userId) throws SQLException;
    void modifyMember(Member member) throws SQLException;
    void removeMember(String userId) throws SQLException;
}
