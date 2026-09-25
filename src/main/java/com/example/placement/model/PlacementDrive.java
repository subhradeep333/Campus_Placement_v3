package com.example.placement.model;

/**
 * CONCRETE MODEL CLASS demonstrating OOP INHERITANCE and ENCAPSULATION.
 * Represents a company placement drive.
 */
public class PlacementDrive extends BaseEntity {
    private String companyName;
    private String role;
    private double packageLpa;
    private double minCgpa;
    private String location;
    private String status; // 'OPEN', 'INTERVIEWING', 'CLOSED'

    public PlacementDrive() {
        super();
    }

    public PlacementDrive(int id, String companyName, String role, double packageLpa, double minCgpa, String location, String status, String createdAt) {
        super(id, createdAt);
        this.companyName = companyName;
        this.role = role;
        this.packageLpa = packageLpa;
        this.minCgpa = minCgpa;
        this.location = location;
        this.status = status;
    }

    // Encapsulated getters & setters
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public double getPackageLpa() { return packageLpa; }
    public void setPackageLpa(double packageLpa) { this.packageLpa = packageLpa; }

    public double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(double minCgpa) { this.minCgpa = minCgpa; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    /**
     * POLYMORPHIC OVERRIDE of abstract method in BaseEntity.
     */
    @Override
    public String toJson() {
        return String.format(
            "{\"id\":%d,\"companyName\":\"%s\",\"role\":\"%s\",\"packageLpa\":%.2f,\"minCgpa\":%.2f,\"location\":\"%s\",\"status\":\"%s\",\"createdAt\":\"%s\"}",
            getId(),
            escapeJson(companyName),
            escapeJson(role),
            packageLpa,
            minCgpa,
            escapeJson(location != null ? location : ""),
            escapeJson(status != null ? status : "OPEN"),
            escapeJson(getCreatedAt() != null ? getCreatedAt() : "")
        );
    }

    public static PlacementDrive parseJson(String json) {
        PlacementDrive drive = new PlacementDrive();
        if (json == null || json.trim().isEmpty()) return drive;

        drive.setCompanyName(extractJsonValue(json, "companyName"));
        drive.setRole(extractJsonValue(json, "role"));
        drive.setLocation(extractJsonValue(json, "location"));
        drive.setStatus(extractJsonValue(json, "status"));

        String pkg = extractJsonValue(json, "packageLpa");
        if (pkg != null && !pkg.isEmpty()) {
            try { drive.setPackageLpa(Double.parseDouble(pkg)); } catch (NumberFormatException ignored) {}
        }

        String cgpa = extractJsonValue(json, "minCgpa");
        if (cgpa != null && !cgpa.isEmpty()) {
            try { drive.setMinCgpa(Double.parseDouble(cgpa)); } catch (NumberFormatException ignored) {}
        }

        String idStr = extractJsonValue(json, "id");
        if (idStr != null && !idStr.isEmpty()) {
            try { drive.setId(Integer.parseInt(idStr)); } catch (NumberFormatException ignored) {}
        }

        return drive;
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
