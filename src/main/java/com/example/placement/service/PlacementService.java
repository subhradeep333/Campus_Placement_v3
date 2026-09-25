package com.example.placement.service;

import com.example.placement.model.PlacementDrive;
import com.example.placement.model.Student;
import com.example.placement.model.StudentApplication;
import com.example.placement.model.User;

import java.sql.SQLException;
import java.util.List;

/**
 * SERVICE INTERFACE demonstrating BUSINESS LOGIC ABSTRACTION.
 */
public interface PlacementService {

    // Auth operations
    User registerUser(User user) throws SQLException;
    User loginUser(String username, String password) throws SQLException;

    // Student operations
    List<Student> getAllStudents() throws SQLException;
    Student getStudentByRoll(String rollNumber) throws SQLException;
    Student saveStudentProfile(Student student) throws SQLException;

    // Drive operations
    List<PlacementDrive> getAllDrives() throws SQLException;
    PlacementDrive getDriveById(int id) throws SQLException;
    PlacementDrive createDrive(PlacementDrive drive) throws SQLException;
    boolean updateDriveStatus(int id, String status) throws SQLException;
    boolean updateDrive(int id, PlacementDrive drive) throws SQLException;
    boolean deleteDrive(int id) throws SQLException;

    // Application operations
    List<StudentApplication> getAllApplications() throws SQLException;
    List<StudentApplication> getApplicationsByRoll(String rollNumber) throws SQLException;
    StudentApplication applyForDrive(StudentApplication app) throws SQLException;
    boolean updateApplicationStatus(int id, String status) throws SQLException;
    boolean deleteApplication(int id) throws SQLException;
}
