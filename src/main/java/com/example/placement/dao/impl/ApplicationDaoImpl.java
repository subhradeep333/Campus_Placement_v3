package com.example.placement.dao.impl;

import com.example.placement.dao.ApplicationDao;
import com.example.placement.model.StudentApplication;
import com.example.placement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CONCRETE IMPLEMENTATION demonstrating POLYMORPHISM and INTERFACE IMPLEMENTATION.
 */
public class ApplicationDaoImpl implements ApplicationDao {

    @Override
    public List<StudentApplication> findAll() throws SQLException {
        List<StudentApplication> list = new ArrayList<>();
        String sql = "SELECT a.id, a.drive_id, d.company_name, d.role, a.student_name, a.roll_number, a.cgpa, a.branch, a.email, a.application_status, a.applied_at " +
                     "FROM student_applications a " +
                     "JOIN placement_drives d ON a.drive_id = d.id " +
                     "ORDER BY a.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToApplication(rs));
            }
        }
        return list;
    }

    @Override
    public StudentApplication findById(Integer id) throws SQLException {
        String sql = "SELECT a.id, a.drive_id, d.company_name, d.role, a.student_name, a.roll_number, a.cgpa, a.branch, a.email, a.application_status, a.applied_at " +
                     "FROM student_applications a " +
                     "JOIN placement_drives d ON a.drive_id = d.id " +
                     "WHERE a.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToApplication(rs);
            }
        }
        return null;
    }

    @Override
    public List<StudentApplication> findByRollNumber(String rollNumber) throws SQLException {
        List<StudentApplication> list = new ArrayList<>();
        String sql = "SELECT a.id, a.drive_id, d.company_name, d.role, a.student_name, a.roll_number, a.cgpa, a.branch, a.email, a.application_status, a.applied_at " +
                     "FROM student_applications a " +
                     "JOIN placement_drives d ON a.drive_id = d.id " +
                     "WHERE LOWER(a.roll_number) = LOWER(?) " +
                     "ORDER BY a.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rollNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToApplication(rs));
                }
            }
        }
        return list;
    }

    @Override
    public StudentApplication save(StudentApplication app) throws SQLException {
        String sql = "INSERT INTO student_applications (drive_id, student_name, roll_number, cgpa, branch, email, application_status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, app.getDriveId());
            stmt.setString(2, app.getStudentName());
            stmt.setString(3, app.getRollNumber());
            stmt.setDouble(4, app.getCgpa());
            stmt.setString(5, app.getBranch());
            stmt.setString(6, app.getEmail());
            stmt.setString(7, app.getApplicationStatus() != null ? app.getApplicationStatus() : "APPLIED");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) app.setId(keys.getInt(1));
                }
            }
            return app;
        }
    }

    @Override
    public boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE student_applications SET application_status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM student_applications WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private StudentApplication mapResultSetToApplication(ResultSet rs) throws SQLException {
        return new StudentApplication(
            rs.getInt("id"),
            rs.getInt("drive_id"),
            rs.getString("company_name"),
            rs.getString("role"),
            rs.getString("student_name"),
            rs.getString("roll_number"),
            rs.getDouble("cgpa"),
            rs.getString("branch"),
            rs.getString("email"),
            rs.getString("application_status"),
            rs.getTimestamp("applied_at") != null ? rs.getTimestamp("applied_at").toString() : ""
        );
    }
}
