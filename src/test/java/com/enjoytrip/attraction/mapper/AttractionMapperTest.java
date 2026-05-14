package com.enjoytrip.attraction.mapper;

import com.enjoytrip.attraction.dto.Gugun;
import com.enjoytrip.attraction.dto.Sido;
import com.enjoytrip.support.AbstractMySqlContainerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AttractionMapperTest extends AbstractMySqlContainerTest {

    @Autowired
    AttractionMapper attractionMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("delete from gugun where sido_code = ?", 1);
        jdbcTemplate.update(
                "insert into gugun (gugun_code, sido_code, gugun_name) values (?, ?, ?), (?, ?, ?)",
                1, 1, "종로구",
                2, 1, "중구"
        );
    }

    @Test
    void selectAllSidoRows() {
        List<Sido> sidos = attractionMapper.selectAllSido();

        assertThat(sidos)
                .extracting(Sido::getSidoName)
                .contains("서울", "제주도");
    }

    @Test
    void selectGugunRowsBySidoCode() {
        List<Gugun> guguns = attractionMapper.selectGugunBySido(1);

        assertThat(guguns)
                .extracting(Gugun::getGugunName)
                .containsExactly("종로구", "중구");
    }
}
