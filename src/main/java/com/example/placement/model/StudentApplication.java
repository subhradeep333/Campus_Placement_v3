package com.example.placement.model;

/**
 * CONCRETE MODEL CLASS demonstrating OOP INHERITANCE and ENCAPSULATION.
 * Represents a student's application for a placement drive.
 */
public class StudentApplication extends BaseEntity {
    private int driveId;
    private String companyName;
    private String role;
    private String studentName;
    private String rollNumber;
    private double cgpa;
    private String branch;
    private String email;
    private String applicationStatus; // 'APPLIED', 'SHORTLISTED', 'SELECTED', 'REJECTED'

    public StudentApplication() {
        super();
    }

    public StudentApplication(int id, int driveId, String companyName, String role, String studentName, String rollNumber, double cgpa, String branch, String email, String applicationStatus, String appliedAt) {
        super(id, appliedAt);
        this.driveId = driveId;
        this.companyName = companyName;
        this.role = role;
        this.studentName = studentName;
        this.rollNumber = rollNumber;
        this.cgpa = cgpa;
        this.branch = branch;
        this.email = email;
        this.applicationStatus = applicationStatus;
    }

    // Encapsulated getters & setters
    public int getDriveId() { return driveId; }
    public void setDriveId(int driveId) { this.driveId = driveId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }

    /**
     * POLYMORPHIC OVERRIDE of abstract method in BaseEntity.
     */
    @Override
    public String toJson() {
        return String.format(
            "{\"id\":%d,\"driveId\":%d,\"companyName\":\"%s\",\"role\":\"%s\",\"studentName\":\"%s\",\"rollNumber\":\"%s\",\"cgpa\":%.2f,\"branch\":\"%s\",\"email\":\"%s\",\"applicationStatus\":\"%s\",\"appliedAt\":\"%s\"}",
            getId(), driveId,
            escapeJson(companyName != null ? companyName : ""),
            escapeJson(role != null ? role : ""),
            escapeJson(studentName),
            escapeJson(rollNumber),
            cgpa,
            escapeJson(branch),
            escapeJson(email),
            escapeJson(applicationStatus != null ? applicationStatus : "APPLIED"),
            escapeJson(getCreatedAt() != null ? getCreatedAt() : "")
        );
    }

    public static StudentApplication parseJson(String json) {
        StudentApplication app = new StudentApplication();
        if (json == null || json.trim().isEmpty()) return app;

        app.setStudentName(extractJsonValue(json, "studentName"));
        app.setRollNumber(extractJsonValue(json, "rollNumber"));
        app.setBranch(extractJsonValue(json, "branch"));
        app.setEmail(extractJsonValue(json, "email"));
        app.setApplicationStatus(extractJsonValue(json, "applicationStatus"));

        String driveIdStr = extractJsonValue(json, "driveId");
        if (driveIdStr != null) {
            try { app.setDriveId(Integer.parseInt(driveIdStr)); } catch (NumberFormatException ignored) {}
        }

        String cgpaStr = extractJsonValue(json, "cgpa");
        if (cgpaStr != null) {
            try { app.setCgpa(Double.parseDouble(cgpaStr)); } catch (NumberFormatException ignored) {}
        }

        String idStr = extractJsonValue(json, "id");
        if (idStr != null) {
            try { app.setId(Integer.parseInt(idStr)); } catch (NumberFormatException ignored) {}
        }

        return app;
    }

    private static String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\":\\s*\"(.*?)\"";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) return unescapeJson(m.group(1));

        pattern = "\"" + key + "\":\\s*([0-9]+\\.?[0-9]*)";
        p = java.util.regex.Pattern.compile(pattern);
        m = p.matcher(json);
        if (m.find()) return m.group(1);

        return null;
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    private static String unescapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
