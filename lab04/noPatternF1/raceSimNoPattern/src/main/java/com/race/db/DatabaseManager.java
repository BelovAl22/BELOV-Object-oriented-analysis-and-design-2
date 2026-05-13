package com.race.db;

import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:race_results.db";

    public static void init() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            String sql = "CREATE TABLE IF NOT EXISTS results (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "driver TEXT, " +
                    "status TEXT, " +
                    "laps INTEGER, " +
                    "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
            conn.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Именно этот метод (save с 3 параметрами) вызывает RaceEngine
    public static void save(String name, String status, int lap) {
        String sql = "INSERT INTO results(driver, status, laps) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, status);
            pstmt.setInt(3, lap);
            pstmt.executeUpdate();
            System.out.println("DB: Result saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}