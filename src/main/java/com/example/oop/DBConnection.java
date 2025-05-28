package com.example.oop;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3307/restaurant", "root", "Admin@1#2$3");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}