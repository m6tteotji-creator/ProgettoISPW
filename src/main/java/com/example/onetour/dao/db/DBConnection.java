package com.example.onetour.dao.db;

import com.example.onetour.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        AppConfig cfg = AppConfig.getInstance();

        String url = cfg.get("db.url", null);
        String user = cfg.get("db.user", null);
        String password = cfg.get("db.password", null);

        if (url == null || url.isBlank()) {
            throw new SQLException("Missing db.url in config.properties");
        }
        if (user == null || user.isBlank()) {
            throw new SQLException("Missing db.user in config.properties");
        }
        if (password == null) {
            throw new SQLException("Missing db.password in config.properties");
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static void closeConnection() {
        // no-op
    }
}
