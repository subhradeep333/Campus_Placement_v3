-- ==========================================================
-- Campus Placement Management System (CV Upload Schema)
-- MySQL + Java JDBC Backend
-- ==========================================================

CREATE DATABASE IF NOT EXISTS placement_db;
USE placement_db;

-- 1. Users Table (Authentication & Role Management)
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL, -- 'STUDENT', 'COMPANY'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Students Table (Student Profiles with CV / Resume upload support)
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(100) UNIQUE NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    cgpa DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    branch VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) DEFAULT '',
    skills TEXT,
    cv_filename VARCHAR(255) DEFAULT '',
    cv_text TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. Placement Drives Table (Company Job Postings)
CREATE TABLE IF NOT EXISTS placement_drives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    package_lpa DECIMAL(5,2) NOT NULL,
    min_cgpa DECIMAL(3,2) DEFAULT 6.00,
    location VARCHAR(255) DEFAULT 'Bangalore',
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN', -- 'OPEN', 'INTERVIEWING', 'CLOSED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Student Applications Table
CREATE TABLE IF NOT EXISTS student_applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    drive_id INT NOT NULL,
    roll_number VARCHAR(100) NOT NULL,
    student_name VARCHAR(255) NOT NULL,
    cgpa DECIMAL(3,2) NOT NULL,
    branch VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    application_status VARCHAR(50) DEFAULT 'APPLIED', -- 'APPLIED', 'SHORTLISTED', 'SELECTED', 'REJECTED'
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_student_drive (drive_id, roll_number),
    FOREIGN KEY (drive_id) REFERENCES placement_drives(id) ON DELETE CASCADE
);

-- 5. Pre-populated Demo Users
INSERT INTO users (id, username, password, full_name, email, role) VALUES
(1, 'rahul', 'password123', 'Rahul Sharma', 'rahul.s@univ.edu', 'STUDENT'),
(2, 'priya', 'password123', 'Priya Patel', 'priya.p@univ.edu', 'STUDENT'),
(3, 'google_recruiter', 'password123', 'Google Campus Hiring', 'recruiter@google.com', 'COMPANY'),
(4, 'msft_recruiter', 'password123', 'Microsoft HR Team', 'hr@microsoft.com', 'COMPANY')
ON DUPLICATE KEY UPDATE username=VALUES(username);

-- 6. Initial Demo Students with Sample CV Text
INSERT INTO students (id, roll_number, student_name, cgpa, branch, email, phone, skills, cv_filename, cv_text) VALUES
(1, 'CS2026-042', 'Rahul Sharma', 8.50, 'Computer Science', 'rahul.s@univ.edu', '+91 9876543210', 'Java, Spring Boot, MySQL, Data Structures, System Design', 'Rahul_Sharma_Resume.pdf', 'Experienced in Java SE, Object-Oriented Programming, Spring Boot REST APIs, MySQL Relational Database, Data Structures and Algorithms.'),
(2, 'EC2026-015', 'Priya Patel', 7.90, 'Electronics', 'priya.p@univ.edu', '+91 9876543211', 'Embedded C, Python, VLSI, MATLAB, Microcontrollers', 'Priya_Patel_CV.pdf', 'Embedded System Engineer skilled in C programming, Python scripting, Signal Processing, VLSI and Circuit Design.'),
(3, 'CS2026-088', 'Aman Verma', 8.20, 'Computer Science', 'aman.v@univ.edu', '+91 9876543212', 'React, Node.js, JavaScript, HTML/CSS, MongoDB', 'Aman_Verma_Resume.pdf', 'Full Stack Web Developer proficient in JavaScript, React.js frontend development, Node.js backend, HTML5, CSS3, and REST API integrations.')
ON DUPLICATE KEY UPDATE student_name=VALUES(student_name);

-- 7. Initial Demo Company Placement Drives
INSERT INTO placement_drives (id, company_name, role, package_lpa, min_cgpa, location, status) VALUES
(1, 'Google', 'Software Development Engineer', 24.50, 8.00, 'Bangalore', 'OPEN'),
(2, 'Microsoft', 'Associate SDE', 18.00, 7.50, 'Hyderabad', 'OPEN'),
(3, 'Amazon', 'System Development Engineer', 16.50, 7.00, 'Bangalore', 'INTERVIEWING'),
(4, 'Infosys', 'Specialist Programmer', 9.50, 6.50, 'Pune', 'OPEN')
ON DUPLICATE KEY UPDATE company_name=VALUES(company_name);

-- 8. Initial Demo Student Applications
INSERT INTO student_applications (id, drive_id, roll_number, student_name, cgpa, branch, email, application_status) VALUES
(1, 1, 'CS2026-042', 'Rahul Sharma', 8.50, 'Computer Science', 'rahul.s@univ.edu', 'SHORTLISTED'),
(2, 2, 'EC2026-015', 'Priya Patel', 7.90, 'Electronics', 'priya.p@univ.edu', 'APPLIED'),
(3, 3, 'CS2026-088', 'Aman Verma', 8.20, 'Computer Science', 'aman.v@univ.edu', 'SELECTED')
ON DUPLICATE KEY UPDATE student_name=VALUES(student_name);
