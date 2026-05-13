package com.enjoytrip.support;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public abstract class AbstractMySqlContainerTest {

    @BeforeAll
    static void startContainer() {
        MySqlTestContainer.MYSQL.start();
    }

    @DynamicPropertySource
    static void registerDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MySqlTestContainer.MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MySqlTestContainer.MYSQL::getUsername);
        registry.add("spring.datasource.password", MySqlTestContainer.MYSQL::getPassword);
    }
}
