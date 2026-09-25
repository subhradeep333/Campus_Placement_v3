package com.example.placement.model;

/**
 * CONCRETE MODEL CLASS demonstrating OOP INHERITANCE and ENCAPSULATION.
 * Extends BaseEntity and encapsulates student profile & CV attributes.
 */
public class Student extends BaseEntity {
    private String rollNumber;
    private String studentName;
    private double cgpa;
    private String branch;
    private String email;
    private String phone;
    private String skills;
    private String cvFilename;
    private String cvText;

    public Student() {
        super();
    }

    public Student(int id, String rollNumber, String studentName, double cgpa, String branch, String email, String phone, String skills, String cvFilename, String cvText, String createdAt) {
        super(id, createdAt);
        this.rollNumber = rollNumber;
        this.studentName = studentName;
        this.cgpa = cgpa;
        this.branch = branch;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
        this.cvFilename = cvFilename;
        this.cvText = cvText;
    }

    // Encapsulated getters and setters
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCvFilename() { return cvFilename; }
    public void setCvFilename(String cvFilename) { this.cvFilename = cvFilename; }

    public String getCvText() { return cvText; }
    public void setCvText(String cvText) { this.cvText = cvText; }

    /**
     * POLYMORPHIC OVERRIDE of abstract method in BaseEntity.
     */
    @Override
    public String toJson() {
        return String.format(
            "{\"id\":%d,\"rollNumber\":\"%s\",\"studentName\":\"%s\",\"cgpa\":%.2f,\"branch\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"skills\":\"%s\",\"cvFilename\":\"%s\",\"cvText\":\"%s\",\"createdAt\":\"%s\"}",
            getId(),
            escapeJson(rollNumber),
            escapeJson(studentName),
            cgpa,
            escapeJson(branch),
            escapeJson(email),
            escapeJson(phone != null ? phone : ""),
            escapeJson(skills != null ? skills : ""),
            escapeJson(cvFilename != null ? cvFilename : ""),
            escapeJson(cvText != null ? cvText : ""),
            escapeJson(getCreatedAt() != null ? getCreatedAt() : "")
        );
    }

    public static Student parseJson(String json) {
        Student s = new Student();
        if (json == null || json.trim().isEmpty()) return s;

        s.setRollNumber(extractJsonValue(json, "rollNumber"));
        s.setStudentName(extractJsonValue(json, "studentName"));
        s.setBranch(extractJsonValue(json, "branch"));
        s.setEmail(extractJsonValue(json, "email"));
        s.setPhone(extractJsonValue(json, "phone"));
        s.setSkills(extractJsonValue(json, "skills"));
        s.setCvFilename(extractJsonValue(json, "cvFilename"));
        s.setCvText(extractJsonValue(json, "cvText"));

        String cgpaStr = extractJsonValue(json, "cgpa");
        if (cgpaStr != null) {
            try { s.setCgpa(Double.parseDouble(cgpaStr)); } catch (NumberFormatException ignored) {}
        }

        String idStr = extractJsonValue(json, "id");
        if (idStr != null) {
            try { s.setId(Integer.parseInt(idStr)); } catch (NumberFormatException ignored) {}
        }

        return s;
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
