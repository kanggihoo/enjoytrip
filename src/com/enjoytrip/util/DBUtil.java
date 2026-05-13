package com.enjoytrip.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL    =
        "jdbc:mysql://localhost:3305/enjoytrip?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER   = "ssafy";
    private static final String PASS   = "ssafy";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try { res.close(); } catch (Exception ignored) {}
            }
        }
    }
}
