package com.example.placement.dao.impl;

import com.example.placement.dao.StudentDao;
import com.example.placement.model.Student;
import com.example.placement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CONCRETE IMPLEMENTATION demonstrating POLYMORPHISM and INTERFACE IMPLEMENTATION.
 * Manages student profiles and CV document data.
 */
public class StudentDaoImpl implements StudentDao {

    @Override
    public List<Student> findAll() throws SQLException {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, roll_number, student_name, cgpa, branch, email, phone, skills, cv_filename, cv_text, updated_at FROM students ORDER BY student_name ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToStudent(rs));
            }
        }
        return list;
    }

    @Override
    public Student findById(Integer id) throws SQLException {
        String sql = "SELECT id, roll_number, student_name, cgpa, branch, email, phone, skills, cv_filename, cv_text, updated_at FROM students WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToStudent(rs);
            }
        }
        return null;
    }

    @Override
    public Student findByRollNumber(String rollNumber) throws SQLException {
        String sql = "SELECT id, roll_number, student_name, cgpa, branch, email, phone, skills, cv_filename, cv_text, updated_at FROM students WHERE LOWER(roll_number) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rollNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToStudent(rs);
            }
        }
        return null;
    }

    @Override
    public Student save(Student student) throws SQLException {
        String sql = "INSERT INTO students (roll_number, student_name, cgpa, branch, email, phone, skills, cv_filename, cv_text) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE student_name = VALUES(student_name), cgpa = VALUES(cgpa), " +
                     "branch = VALUES(branch), email = VALUES(email), phone = VALUES(phone), skills = VALUES(skills), " +
                     "cv_filename = VALUES(cv_filename), cv_text = VALUES(cv_text)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getRollNumber());
            stmt.setString(2, student.getStudentName());
            stmt.setDouble(3, student.getCgpa());
            stmt.setString(4, student.getBranch());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getPhone() != null ? student.getPhone() : "");
            stmt.setString(7, student.getSkills() != null ? student.getSkills() : "");
            stmt.setString(8, student.getCvFilename() != null ? student.getCvFilename() : "");
            stmt.setString(9, student.getCvText() != null ? student.getCvText() : "");

            stmt.executeUpdate();
            return findByRollNumber(student.getRollNumber());
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        return new Student(
            rs.getInt("id"),
            rs.getString("roll_number"),
            rs.getString("student_name"),
            rs.getDouble("cgpa"),
            rs.getString("branch"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("skills"),
            rs.getString("cv_filename"),
            rs.getString("cv_text"),
            rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toString() : ""
        );
    }
}
