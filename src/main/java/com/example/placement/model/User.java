package com.example.placement.model;

/**
 * CONCRETE MODEL CLASS demonstrating OOP INHERITANCE and ENCAPSULATION.
 * Represents a User (Student or Company) in MySQL 'users' table.
 */
public class User extends BaseEntity {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String role; // 'STUDENT' or 'COMPANY'

    public User() {
        super();
    }

    public User(int id, String username, String password, String fullName, String email, String role, String createdAt) {
        super(id, createdAt);
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Encapsulated getters & setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    /**
     * POLYMORPHIC OVERRIDE of abstract method in BaseEntity.
     * Hides sensitive fields like password when converting to JSON.
     */
    @Override
    public String toJson() {
        return String.format(
            "{\"id\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"createdAt\":\"%s\"}",
            getId(),
            escapeJson(username),
            escapeJson(fullName),
            escapeJson(email),
            escapeJson(role),
            escapeJson(getCreatedAt() != null ? getCreatedAt() : "")
        );
    }

    public static User parseJson(String json) {
        User u = new User();
        if (json == null || json.trim().isEmpty()) return u;

        u.setUsername(extractJsonValue(json, "username"));
        u.setPassword(extractJsonValue(json, "password"));
        u.setFullName(extractJsonValue(json, "fullName"));
        u.setEmail(extractJsonValue(json, "email"));
        u.setRole(extractJsonValue(json, "role"));

        String idStr = extractJsonValue(json, "id");
        if (idStr != null) {
            try { u.setId(Integer.parseInt(idStr)); } catch (NumberFormatException ignored) {}
        }

        return u;
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
