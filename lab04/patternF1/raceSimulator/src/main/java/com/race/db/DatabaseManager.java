package com.race.db;

import java.sql.*;

public class DatabaseManager {
    // Имя файла базы данных (создастся в корне проекта)
    private static final String URL = "jdbc:sqlite:race_results.db";

    // 1. Инициализация (создание таблицы, если её нет)
    public static void init() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                String sql = "CREATE TABLE IF NOT EXISTS results (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "winner TEXT NOT NULL, " +
                        "laps INTEGER, " +
                        "date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
                System.out.println("DB: Database initialized.");
            }
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
        }
    }

    // 2. Сохранение результата
    public static void saveResult(String winnerName, int totalLaps) {
        String sql = "INSERT INTO results(winner, laps) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, winnerName);
            pstmt.setInt(2, totalLaps);
            pstmt.executeUpdate();
            System.out.println("DB: Result saved to SQLite!");
        } catch (SQLException e) {
            System.err.println("DB Save Error: " + e.getMessage());
        }
    }
}