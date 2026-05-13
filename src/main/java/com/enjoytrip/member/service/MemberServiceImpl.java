package com.enjoytrip.member.service;

import com.enjoytrip.member.dao.MemberDao;
import com.enjoytrip.member.dao.MemberDaoImpl;
import com.enjoytrip.member.dto.Member;

import java.sql.SQLException;

public class MemberServiceImpl implements MemberService {

    private final MemberDao dao = new MemberDaoImpl();

    @Override
    public void join(Member member) throws SQLException, IllegalArgumentException {
        if (dao.existsUserId(member.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        dao.insertMember(member);
    }

    @Override
    public Member login(String userId, String userPw) throws SQLException {
        return dao.selectMemberByIdAndPw(userId, userPw);
    }

    @Override
    public Member getMemberById(String userId) throws SQLException {
        return dao.selectMemberById(userId);
    }

    @Override
    public void modifyMember(Member member) throws SQLException {
        dao.updateMember(member);
    }

    @Override
    public void removeMember(String userId) throws SQLException {
        dao.deleteMember(userId);
    }
}
