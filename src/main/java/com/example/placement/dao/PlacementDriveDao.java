package com.example.placement.dao;

import com.example.placement.model.PlacementDrive;
import java.sql.SQLException;

/**
 * INTERFACE demonstrating OOP INHERITANCE for PlacementDrive entity.
 */
public interface PlacementDriveDao extends GenericDao<PlacementDrive, Integer> {
    boolean updateStatus(int id, String status) throws SQLException;
    boolean updateDrive(int id, PlacementDrive drive) throws SQLException;
}
