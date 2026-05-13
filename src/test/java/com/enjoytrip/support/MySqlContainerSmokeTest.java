package com.enjoytrip.support;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MySqlContainerSmokeTest extends AbstractMySqlContainerTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void schemaIsLoaded() {
        Integer count = jdbcTemplate.queryForObject("select count(*) from member", Integer.class);

        assertThat(count).isNotNull();
        assertThat(count).isGreaterThanOrEqualTo(1);
    }
}
