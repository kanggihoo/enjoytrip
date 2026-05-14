package com.enjoytrip.member.service;

import com.enjoytrip.common.exception.BusinessException;
import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.member.dto.Member;
import com.enjoytrip.member.mapper.MemberMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MemberServiceImplTest {

    private final MemberMapper mapper = mock(MemberMapper.class);
    private final MemberService service = new MemberServiceImpl(mapper);

    @Test
    void joinThrowsDuplicateUserId() {
        Member member = Member.builder().userId("ssafy").userPw("pw").userName("name").build();
        when(mapper.existsUserId("ssafy")).thenReturn(true);

        assertThatThrownBy(() -> service.join(member))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATE_USER_ID);
    }

    @Test
    void loginReturnsMemberWhenCredentialsMatch() {
        Member member = Member.builder().userId("ssafy").userName("관리자").build();
        when(mapper.selectMemberByIdAndPw("ssafy", "ssafy")).thenReturn(member);

        Member result = service.login("ssafy", "ssafy");

        assertThat(result.getUserId()).isEqualTo("ssafy");
    }

    @Test
    void removeMemberDeletesById() {
        service.removeMember("ssafy");

        verify(mapper).deleteMember("ssafy");
    }
}
