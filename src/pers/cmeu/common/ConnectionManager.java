package pers.cmeu.common;

import java.sql.Connection;
import java.sql.DriverManager;


public class ConnectionManager {

    private static final String DB_URL = "jdbc:sqlite:./config/sqlite3.db";

    public static Connection getConnection() throws Exception {
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection(DB_URL);
        return conn;
    }
}
