package com.enjoytrip.member.service;

import com.enjoytrip.common.exception.BusinessException;
import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.member.dto.Member;
import com.enjoytrip.member.mapper.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    public MemberServiceImpl(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    @Transactional
    public void join(Member member) {
        if (memberMapper.existsUserId(member.getUserId())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USER_ID);
        }
        memberMapper.insertMember(member);
    }

    @Override
    public Member login(String userId, String userPw) {
        return memberMapper.selectMemberByIdAndPw(userId, userPw);
    }

    @Override
    public Member getMemberById(String userId) {
        return memberMapper.selectMemberById(userId);
    }

    @Override
    @Transactional
    public void modifyMember(Member member) {
        memberMapper.updateMember(member);
    }

    @Override
    @Transactional
    public void removeMember(String userId) {
        memberMapper.deleteMember(userId);
    }
}
