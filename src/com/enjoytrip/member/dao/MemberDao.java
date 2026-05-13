package com.enjoytrip.member.dao;

import com.enjoytrip.member.dto.Member;
import java.sql.SQLException;

public interface MemberDao {
    void insertMember(Member member) throws SQLException;
    Member selectMemberById(String userId) throws SQLException;
    Member selectMemberByIdAndPw(String userId, String userPw) throws SQLException;
    void updateMember(Member member) throws SQLException;
    void deleteMember(String userId) throws SQLException;
    boolean existsUserId(String userId) throws SQLException;
}
