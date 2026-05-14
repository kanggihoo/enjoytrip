package com.enjoytrip.member.mapper;

import com.enjoytrip.member.dto.Member;
import com.enjoytrip.support.AbstractMySqlContainerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberMapperTest extends AbstractMySqlContainerTest {

    @Autowired
    MemberMapper memberMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from member where user_id in (?, ?)", "mapper-user", "mapper-updated");
    }

    @Test
    void selectSeedMember() {
        Member result = memberMapper.selectMemberById("ssafy");

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("ssafy");
        assertThat(result.getEmail()).isEqualTo("admin@enjoytrip.com");
    }

    @Test
    void insertAndSelectMember() {
        Member member = Member.builder()
                .userId("mapper-user")
                .userPw("pw")
                .userName("테스터")
                .email("mapper@example.com")
                .build();

        memberMapper.insertMember(member);

        Member result = memberMapper.selectMemberById("mapper-user");
        assertThat(result.getUserId()).isEqualTo("mapper-user");
        assertThat(result.getUserName()).isEqualTo("테스터");
        assertThat(result.getEmail()).isEqualTo("mapper@example.com");
        assertThat(result.getJoinDate()).isNotNull();
    }

    @Test
    void updateAndDeleteMember() {
        memberMapper.insertMember(Member.builder()
                .userId("mapper-updated")
                .userPw("pw")
                .userName("변경전")
                .email("before@example.com")
                .build());

        memberMapper.updateMember(Member.builder()
                .userId("mapper-updated")
                .userPw("new-pw")
                .userName("변경후")
                .email("after@example.com")
                .build());

        Member updated = memberMapper.selectMemberByIdAndPw("mapper-updated", "new-pw");
        assertThat(updated.getUserName()).isEqualTo("변경후");
        assertThat(memberMapper.existsUserId("mapper-updated")).isTrue();

        memberMapper.deleteMember("mapper-updated");

        assertThat(memberMapper.existsUserId("mapper-updated")).isFalse();
    }
}
