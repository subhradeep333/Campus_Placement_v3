package com.example.placement.dao.impl;

import com.example.placement.dao.UserDao;
import com.example.placement.model.User;
import com.example.placement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CONCRETE DAO IMPLEMENTATION for User Authentication.
 */
public class UserDaoImpl implements UserDao {

    @Override
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, username, password, full_name, email, role, created_at FROM users ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        }
        return list;
    }

    @Override
    public User findById(Integer id) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, role, created_at FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    @Override
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, role, created_at FROM users WHERE LOWER(username) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, username, password, full_name, email, role, created_at FROM users WHERE LOWER(email) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToUser(rs);
            }
        }
        return null;
    }

    @Override
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole() != null ? user.getRole() : "STUDENT");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) user.setId(keys.getInt(1));
                }
            }
            return user;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("full_name"),
            rs.getString("email"),
            rs.getString("role"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toString() : ""
        );
    }
}
