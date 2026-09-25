# 🎓 Campus Placement Management Portal (Student & Company Roles)

A complete, lightweight **Campus Placement Portal** built with **HTML, CSS, and Vanilla JavaScript** on the frontend, and pure **Java with JDBC** on the backend connecting directly to **MySQL**.

---

## 🔒 Role-Based Access Overview

### 👨‍🎓 Student Access:
- **Apply to Drives**: Browse active company placement drives and apply.
- **Edit Profile**: Update student profile (Full Name, Roll Number, CGPA, Branch, Email, Phone, Skills) stored in MySQL.
- **Track Status**: Look up live application statuses (`APPLIED`, `SHORTLISTED`, `SELECTED`, `REJECTED`).
- *No access to delete drives, post drives, or view other students' private data.*

### 🏢 Company / Recruiter Access (Full Access):
- **Post Drives**: Create new campus recruitment drives with CTC packages & minimum CGPA requirements.
- **Manage Drives**: Update drive status (`OPEN`, `INTERVIEWING`, `CLOSED`) or delete drives.
- **Review Student Applications**: Access full student details (CGPA, Branch, Phone, Skills) and update application status (**Shortlist**, **Select**, **Reject**).
- **Student Directory**: Browse directory of registered students in MySQL.

---

## 📁 Directory Structure

```text
ppp/
├── schema.sql                         # MySQL database & table creation script
├── run.sh                             # Execution script (compiles Java & starts server)
├── pom.xml                            # Maven configuration file
├── lib/
│   └── mysql-connector-j-9.0.0.jar     # MySQL JDBC Driver JAR
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── placement/
        │               ├── Main.java           # Java HTTP Server & API Router
        │               ├── Database.java       # JDBC Connection Manager
        │               ├── Student.java        # Student Profile Model
        │               ├── StudentDao.java     # JDBC Student Profile Operations
        │               ├── PlacementDrive.java # Placement Drive Model
        │               ├── PlacementDao.java   # JDBC Placement Drives Operations
        │               ├── StudentApplication.java # Application Model
        │               └── ApplicationDao.java # JDBC Applications Operations
        └── resources/
            └── public/
                ├── index.html                 # Main Web Portal Page
                ├── style.css                  # Custom UI Stylesheet
                └── app.js                     # Frontend REST API Logic
```

---

## 🚀 How to Run

1. **Import Database Schema in MySQL**:
   ```bash
   mysql -u root -psubhradeep3 < schema.sql
   ```

2. **Start the Java Server**:
   ```bash
   ./run.sh
   ```

3. **Open Web Browser**:
   👉 **[http://localhost:8080](http://localhost:8080)**
