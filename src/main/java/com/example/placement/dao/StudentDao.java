package com.example.placement.dao;

import com.example.placement.model.Student;
import java.sql.SQLException;

/**
 * INTERFACE demonstrating OOP INHERITANCE across interfaces.
 * Extends GenericDao for Student entity.
 */
public interface StudentDao extends GenericDao<Student, Integer> {
    Student findByRollNumber(String rollNumber) throws SQLException;
}
