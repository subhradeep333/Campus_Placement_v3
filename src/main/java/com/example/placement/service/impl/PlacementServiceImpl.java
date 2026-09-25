package com.example.placement.service.impl;

import com.example.placement.dao.ApplicationDao;
import com.example.placement.dao.PlacementDriveDao;
import com.example.placement.dao.StudentDao;
import com.example.placement.dao.UserDao;
import com.example.placement.dao.impl.ApplicationDaoImpl;
import com.example.placement.dao.impl.PlacementDriveDaoImpl;
import com.example.placement.dao.impl.StudentDaoImpl;
import com.example.placement.dao.impl.UserDaoImpl;
import com.example.placement.model.PlacementDrive;
import com.example.placement.model.Student;
import com.example.placement.model.StudentApplication;
import com.example.placement.model.User;
import com.example.placement.service.PlacementService;

import java.sql.SQLException;
import java.util.List;

/**
 * CONCRETE SERVICE IMPLEMENTATION demonstrating POLYMORPHISM and ENCAPSULATION.
 */
public class PlacementServiceImpl implements PlacementService {

    // Encapsulated DAO references (Polymorphism: referencing Interfaces)
    private final UserDao userDao;
    private final StudentDao studentDao;
    private final PlacementDriveDao driveDao;
    private final ApplicationDao appDao;

    public PlacementServiceImpl() {
        this.userDao = new UserDaoImpl();
        this.studentDao = new StudentDaoImpl();
        this.driveDao = new PlacementDriveDaoImpl();
        this.appDao = new ApplicationDaoImpl();
    }

    @Override
    public User registerUser(User user) throws SQLException {
        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        return userDao.save(user);
    }

    @Override
    public User loginUser(String username, String password) throws SQLException {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public List<Student> getAllStudents() throws SQLException {
        return studentDao.findAll();
    }

    @Override
    public Student getStudentByRoll(String rollNumber) throws SQLException {
        return studentDao.findByRollNumber(rollNumber);
    }

    @Override
    public Student saveStudentProfile(Student student) throws SQLException {
        return studentDao.save(student);
    }

    @Override
    public List<PlacementDrive> getAllDrives() throws SQLException {
        return driveDao.findAll();
    }

    @Override
    public PlacementDrive getDriveById(int id) throws SQLException {
        return driveDao.findById(id);
    }

    @Override
    public PlacementDrive createDrive(PlacementDrive drive) throws SQLException {
        return driveDao.save(drive);
    }

    @Override
    public boolean updateDriveStatus(int id, String status) throws SQLException {
        return driveDao.updateStatus(id, status);
    }

    @Override
    public boolean updateDrive(int id, PlacementDrive drive) throws SQLException {
        return driveDao.updateDrive(id, drive);
    }

    @Override
    public boolean deleteDrive(int id) throws SQLException {
        return driveDao.delete(id);
    }

    @Override
    public List<StudentApplication> getAllApplications() throws SQLException {
        return appDao.findAll();
    }

    @Override
    public List<StudentApplication> getApplicationsByRoll(String rollNumber) throws SQLException {
        return appDao.findByRollNumber(rollNumber);
    }

    @Override
    public StudentApplication applyForDrive(StudentApplication app) throws SQLException {
        if (appDao.existsByDriveIdAndRollNumber(app.getDriveId(), app.getRollNumber())) {
            throw new IllegalArgumentException("Student (" + app.getRollNumber() + ") has already applied for this company drive.");
        }
        return appDao.save(app);
    }

    @Override
    public boolean updateApplicationStatus(int id, String status) throws SQLException {
        return appDao.updateStatus(id, status);
    }

    @Override
    public boolean deleteApplication(int id) throws SQLException {
        return appDao.delete(id);
    }
}
