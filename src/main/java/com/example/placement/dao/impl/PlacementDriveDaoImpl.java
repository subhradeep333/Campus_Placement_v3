package com.example.placement.dao.impl;

import com.example.placement.dao.PlacementDriveDao;
import com.example.placement.model.PlacementDrive;
import com.example.placement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CONCRETE IMPLEMENTATION demonstrating POLYMORPHISM and INTERFACE IMPLEMENTATION.
 */
public class PlacementDriveDaoImpl implements PlacementDriveDao {

    @Override
    public List<PlacementDrive> findAll() throws SQLException {
        List<PlacementDrive> list = new ArrayList<>();
        String sql = "SELECT id, company_name, role, package_lpa, min_cgpa, location, status, created_at FROM placement_drives ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToDrive(rs));
            }
        }
        return list;
    }

    @Override
    public PlacementDrive findById(Integer id) throws SQLException {
        String sql = "SELECT id, company_name, role, package_lpa, min_cgpa, location, status, created_at FROM placement_drives WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapResultSetToDrive(rs);
            }
        }
        return null;
    }

    @Override
    public PlacementDrive save(PlacementDrive drive) throws SQLException {
        String sql = "INSERT INTO placement_drives (company_name, role, package_lpa, min_cgpa, location, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, drive.getCompanyName());
            stmt.setString(2, drive.getRole());
            stmt.setDouble(3, drive.getPackageLpa());
            stmt.setDouble(4, drive.getMinCgpa() > 0 ? drive.getMinCgpa() : 6.00);
            stmt.setString(5, drive.getLocation() != null && !drive.getLocation().isEmpty() ? drive.getLocation() : "Bangalore");
            stmt.setString(6, drive.getStatus() != null && !drive.getStatus().isEmpty() ? drive.getStatus() : "OPEN");

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) drive.setId(keys.getInt(1));
                }
            }
            return drive;
        }
    }

    @Override
    public boolean updateDrive(int id, PlacementDrive drive) throws SQLException {
        String sql = "UPDATE placement_drives SET company_name = ?, role = ?, package_lpa = ?, min_cgpa = ?, location = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, drive.getCompanyName());
            stmt.setString(2, drive.getRole());
            stmt.setDouble(3, drive.getPackageLpa());
            stmt.setDouble(4, drive.getMinCgpa());
            stmt.setString(5, drive.getLocation());
            stmt.setString(6, drive.getStatus());
            stmt.setInt(7, id);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateStatus(int id, String status) throws SQLException {
        String sql = "UPDATE placement_drives SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, id);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM placement_drives WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private PlacementDrive mapResultSetToDrive(ResultSet rs) throws SQLException {
        return new PlacementDrive(
            rs.getInt("id"),
            rs.getString("company_name"),
            rs.getString("role"),
            rs.getDouble("package_lpa"),
            rs.getDouble("min_cgpa"),
            rs.getString("location"),
            rs.getString("status"),
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toString() : ""
        );
    }
}
