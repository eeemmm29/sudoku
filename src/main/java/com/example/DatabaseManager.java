package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    String url = "jdbc:sqlite:src/main/resources/com/example/leaderboard.db";

    public void createDatabaseTable() {
        String sql = "CREATE TABLE IF NOT EXISTS leaderboard (" +
                     "id INTEGER PRIMARY KEY, " +
                     "player_name VARCHAR NOT NULL, " +
                     "score INTEGER NOT NULL, " +
                     "time INTEGER NOT NULL, " +
                     "time_formatted VARCHAR NOT NULL)";
        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {
                stmt.execute(sql);
                System.out.println("Table created successfully.");
            } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insertData(String player_name, int score, int time, String time_formatted) {
        String sql = "INSERT INTO leaderboard(player_name, score, time, time_formatted) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, player_name);
                pstmt.setInt(2, score);
                pstmt.setInt(3, time);
                pstmt.setString(4, time_formatted);
                pstmt.executeUpdate();
                
                System.out.println("Data inserted successfully.");
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
    }

    public List<Score> getTopFiveScores() {
        String sql = "SELECT player_name, score, time_formatted FROM leaderboard " +
                     "ORDER BY score DESC, time ASC LIMIT 5";

        List<Score> topScores = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String playerName = rs.getString("player_name");
                int score = rs.getInt("score");
                String time_formatted = rs.getString("time_formatted");
                topScores.add(new Score(playerName, score, time_formatted));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return topScores;
    }
}

class Score {
    private String playerName;
    private int score;
    private String time_formatted;

    Score(String playerName, int score, String time_formatted) {
        this.playerName = playerName;
        this.score = score;
        this.time_formatted = time_formatted;
    }

    @Override
    public String toString() {
        return playerName + "\t" + score + "\t" + time_formatted;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public String getTime_formatted() {
        return time_formatted;
    }
}