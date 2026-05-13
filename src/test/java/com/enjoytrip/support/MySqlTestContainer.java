package com.enjoytrip.support;

import org.testcontainers.containers.MySQLContainer;

public final class MySqlTestContainer {

    public static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8")
            .withDatabaseName("enjoytrip")
            .withUsername("ssafy")
            .withPassword("ssafy")
            .withInitScript("schema.sql");

    private MySqlTestContainer() {
    }
}
