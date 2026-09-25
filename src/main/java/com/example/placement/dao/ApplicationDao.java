package com.example.placement.dao;

import com.example.placement.model.StudentApplication;
import java.sql.SQLException;
import java.util.List;

/**
 * INTERFACE demonstrating OOP INHERITANCE for StudentApplication entity.
 */
public interface ApplicationDao extends GenericDao<StudentApplication, Integer> {
    List<StudentApplication> findByRollNumber(String rollNumber) throws SQLException;
    boolean updateStatus(int id, String status) throws SQLException;
}
