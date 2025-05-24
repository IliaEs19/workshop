package com.tilldawn.Models.SaveDatas;

import com.badlogic.gdx.Gdx;
import com.tilldawn.Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteUserStorage implements UserDataStorage {
    private static final String DB_NAME = "data/tilldawn.db";
    private Connection connection;

    public SqliteUserStorage() {
        try {

            Class.forName("org.sqlite.JDBC");


            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);


            createTablesIfNotExists();

            Gdx.app.log("SqliteUserStorage", "SQLite database initialized successfully");
        } catch (ClassNotFoundException | SQLException e) {
            Gdx.app.error("SqliteUserStorage", "Error initializing SQLite database", e);
        }
    }

    private void createTablesIfNotExists() {
        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY, " +
                    "password TEXT NOT NULL, " +
                    "security_question TEXT, " +
                    "security_answer TEXT, " +
                    "avatar_path TEXT, " +
                    "last_weapon_used TEXT, " +
                    "last_game_time INTEGER DEFAULT 0, " +
                    "last_hero_used TEXT, " +
                    "high_score INTEGER DEFAULT 0, " +
                    "total_games_played INTEGER DEFAULT 0, " +
                    "total_kills INTEGER DEFAULT 0, " +
                    "longest_survival_time REAL DEFAULT 0, " +
                    "total_survival_time REAL DEFAULT 0" +
                    ");"
            );

            Gdx.app.log("SqliteUserStorage", "Database tables created/verified");
        } catch (SQLException e) {
            Gdx.app.error("SqliteUserStorage", "Error creating database tables", e);
        }
    }

    @Override
    public void saveUser(User user) {
        try {

            if (userExists(user.getUserName())) {

                updateUser(user);
            } else {

                insertUser(user);
            }
        } catch (SQLException e) {
            Gdx.app.error("SqliteUserStorage", "Error saving user to database", e);
        }
    }

    private void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, security_question, security_answer, avatar_path, " +
            "last_weapon_used, last_game_time, last_hero_used, high_score, total_games_played, " +
            "total_kills, longest_survival_time, total_survival_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserName());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getSecurityQuestion());
            pstmt.setString(4, user.getSecurityAnswer());
            pstmt.setString(5, user.getAvatarPath());
            pstmt.setString(6, user.getLastWeaponUsed());
            pstmt.setInt(7, user.getLastGameTime());
            pstmt.setString(8, user.getLastHeroUsed());
            pstmt.setInt(9, user.getHighScore());
            pstmt.setInt(10, user.getTotalGamesPlayed());
            pstmt.setInt(11, user.getTotalKills());
            pstmt.setFloat(12, user.getLongestSurvivalTime());
            pstmt.setFloat(13, user.getTotalSurvivalTime());

            pstmt.executeUpdate();
            Gdx.app.log("SqliteUserStorage", "User " + user.getUserName() + " inserted into database");
        }
    }

    private void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET password = ?, security_question = ?, security_answer = ?, avatar_path = ?, " +
            "last_weapon_used = ?, last_game_time = ?, last_hero_used = ?, high_score = ?, total_games_played = ?, " +
            "total_kills = ?, longest_survival_time = ?, total_survival_time = ? WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getSecurityQuestion());
            pstmt.setString(3, user.getSecurityAnswer());
            pstmt.setString(4, user.getAvatarPath());
            pstmt.setString(5, user.getLastWeaponUsed());
            pstmt.setInt(6, user.getLastGameTime());
            pstmt.setString(7, user.getLastHeroUsed());
            pstmt.setInt(8, user.getHighScore());
            pstmt.setInt(9, user.getTotalGamesPlayed());
            pstmt.setInt(10, user.getTotalKills());
            pstmt.setFloat(11, user.getLongestSurvivalTime());
            pstmt.setFloat(12, user.getTotalSurvivalTime());
            pstmt.setString(13, user.getUserName());

            pstmt.executeUpdate();
            Gdx.app.log("SqliteUserStorage", "User " + user.getUserName() + " updated in database");
        }
    }

    @Override
    public User loadUser(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("security_question"),
                        rs.getString("security_answer"),
                        rs.getString("avatar_path")
                    );

                    user.setLastWeaponUsed(rs.getString("last_weapon_used"));
                    user.setLastGameTime(rs.getInt("last_game_time"));
                    user.setLastHeroUsed(rs.getString("last_hero_used"));
                    user.setHighScore(rs.getInt("high_score"));
                    user.setTotalGamesPlayed(rs.getInt("total_games_played"));
                    user.setTotalKills(rs.getInt("total_kills"));
                    user.updateLongestSurvivalTime(rs.getFloat("longest_survival_time"));
                    user.addSurvivalTime(rs.getFloat("total_survival_time"));

                    return user;
                }
            }
        } catch (SQLException e) {
            Gdx.app.error("SqliteUserStorage", "Error loading user from database", e);
        }

        return null;
    }

    @Override
    public List<User> loadAllUsers() {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("security_question"),
                    rs.getString("security_answer"),
                    rs.getString("avatar_path")
                );

                user.setLastWeaponUsed(rs.getString("last_weapon_used"));
                user.setLastGameTime(rs.getInt("last_game_time"));
                user.setLastHeroUsed(rs.getString("last_hero_used"));
                user.setHighScore(rs.getInt("high_score"));
                user.setTotalGamesPlayed(rs.getInt("total_games_played"));
                user.setTotalKills(rs.getInt("total_kills"));
                user.updateLongestSurvivalTime(rs.getFloat("longest_survival_time"));
                user.addSurvivalTime(rs.getFloat("total_survival_time"));

                userList.add(user);
            }

            Gdx.app.log("SqliteUserStorage", "Loaded " + userList.size() + " users from database");
        } catch (SQLException e) {
            Gdx.app.error("SqliteUserStorage", "Error loading users from database", e);
        }

        return userList;
    }

    @Override
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            Gdx.app.error("SqliteUserStorage", "Error deleting user from database", e);
            return false;
        }
    }

    @Override
    public boolean userExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            Gdx.app.error("SqliteUserStorage", "Error checking if user exists", e);
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                Gdx.app.log("SqliteUserStorage", "Database connection closed");
            }
        } catch (SQLException e) {
            Gdx.app.error("SqliteUserStorage", "Error closing database connection", e);
        }
    }
}
