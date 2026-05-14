package com.enjoytrip.member.mapper;

import com.enjoytrip.member.dto.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    void insertMember(Member member);
    Member selectMemberById(String userId);
    Member selectMemberByIdAndPw(String userId, String userPw);
    void updateMember(Member member);
    void deleteMember(String userId);
    boolean existsUserId(String userId);
}
