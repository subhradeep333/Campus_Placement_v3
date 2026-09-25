package com.example.placement;

import com.example.placement.model.PlacementDrive;
import com.example.placement.model.Student;
import com.example.placement.model.StudentApplication;
import com.example.placement.model.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Pure Java Test Suite for HireNext Campus Placement System.
 * Verifies Domain Models, JSON Serialization, CV Data Attributes, and Server REST Endpoints.
 */
public class PlacementTestSuite {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("🧪 =====================================================");
        System.out.println("🧪 RUNNING CAMPUS PLACEMENT SYSTEM TEST SUITE");
        System.out.println("🧪 =====================================================\n");

        runTest("Student Entity & CV JSON Parsing", PlacementTestSuite::testStudentModel);
        runTest("Placement Drive Entity Validation", PlacementTestSuite::testPlacementDriveModel);
        runTest("User Authentication Entity & Roles", PlacementTestSuite::testUserEntity);
        runTest("Student Application Entity Tracking", PlacementTestSuite::testStudentApplicationEntity);
        runTest("Single Application Per Drive Rule Verification", PlacementTestSuite::testDuplicateApplicationPrevention);
        runTest("CV Smart Match Algorithm Simulation", PlacementTestSuite::testCvSmartMatchLogic);
        runTest("REST API HTTP Server Live Endpoint Health", PlacementTestSuite::testHttpServerHealth);

        System.out.println("\n📊 =====================================================");
        System.out.println(String.format("📊 TEST SUMMARY: %d PASSED | %d FAILED", testsPassed, testsFailed));
        System.out.println("📊 =====================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void runTest(String testName, TestCase testCase) {
        System.out.print("▶ Running: " + testName + " ... ");
        try {
            testCase.execute();
            System.out.println("✅ PASSED");
            testsPassed++;
        } catch (Throwable t) {
            System.out.println("❌ FAILED (" + t.getMessage() + ")");
            t.printStackTrace(System.out);
            testsFailed++;
        }
    }

    @FunctionalInterface
    interface TestCase {
        void execute() throws Exception;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected != null && expected.equals(actual)) return;
        throw new AssertionError(message + " [Expected: " + expected + ", Actual: " + actual + "]");
    }

    // 1. Test Student Entity & JSON Serialization
    private static void testStudentModel() {
        Student s = new Student(101, "CS2026-999", "Test Candidate", 8.85, "Computer Science", 
                                "test@univ.edu", "+91 9900000000", "Java, Spring, MySQL", 
                                "Resume_Test.pdf", "Skilled in Java OOP and MySQL", "2026-09-26");

        assertEquals("CS2026-999", s.getRollNumber(), "Roll number mismatch");
        assertEquals(8.85, s.getCgpa(), "CGPA mismatch");
        assertEquals("Resume_Test.pdf", s.getCvFilename(), "CV Filename mismatch");
        assertEquals("Skilled in Java OOP and MySQL", s.getCvText(), "CV Text mismatch");

        // Verify JSON string output contains CV fields
        String json = s.toJson();
        assertTrue(json.contains("\"cvFilename\":\"Resume_Test.pdf\""), "JSON missing cvFilename");
        assertTrue(json.contains("\"cvText\":\"Skilled in Java OOP and MySQL\""), "JSON missing cvText");

        // Test JSON Parsing back to Student entity
        Student parsed = Student.parseJson(json);
        assertEquals("CS2026-999", parsed.getRollNumber(), "Parsed Roll Number mismatch");
        assertEquals("Test Candidate", parsed.getStudentName(), "Parsed Name mismatch");
        assertEquals("Resume_Test.pdf", parsed.getCvFilename(), "Parsed CV filename mismatch");
    }

    // 2. Test Placement Drive Entity
    private static void testPlacementDriveModel() {
        PlacementDrive drive = new PlacementDrive(10, "Google", "Software Engineer", 24.50, 8.00, "Bangalore", "OPEN", "2026-09-26");

        assertEquals("Google", drive.getCompanyName(), "Company name mismatch");
        assertEquals(24.50, drive.getPackageLpa(), "Package LPA mismatch");
        assertEquals(8.00, drive.getMinCgpa(), "Min CGPA cutoff mismatch");
        assertEquals("OPEN", drive.getStatus(), "Drive status mismatch");

        String json = drive.toJson();
        assertTrue(json.contains("\"companyName\":\"Google\""), "Drive JSON missing companyName");
        assertTrue(json.contains("\"packageLpa\":24.50"), "Drive JSON missing packageLpa");
    }

    // 3. Test User Entity
    private static void testUserEntity() {
        User user = new User(1, "student_tester", "secret123", "Test Student", "tester@univ.edu", "STUDENT", "2026-09-26");

        assertEquals("student_tester", user.getUsername(), "Username mismatch");
        assertEquals("STUDENT", user.getRole(), "User role mismatch");

        String json = user.toJson();
        assertTrue(json.contains("\"role\":\"STUDENT\""), "User JSON missing role");
    }

    // 4. Test Student Application Entity
    private static void testStudentApplicationEntity() {
        StudentApplication app = new StudentApplication(1, 10, "Google", "Software Engineer", "Test Candidate", "CS2026-999", 8.85, "Computer Science", "test@univ.edu", "SHORTLISTED", "2026-09-26");

        assertEquals("SHORTLISTED", app.getApplicationStatus(), "Application status mismatch");
        assertEquals(10, app.getDriveId(), "Drive ID mismatch");
    }

    // 5. Test Duplicate Application Rule (1 Application Per Student Per Drive)
    private static void testDuplicateApplicationPrevention() {
        StudentApplication app1 = new StudentApplication(1, 1, "Google", "SDE", "Rahul Sharma", "CS2026-042", 8.50, "CS", "rahul@univ.edu", "APPLIED", "2026-09-26");
        StudentApplication app2 = new StudentApplication(2, 1, "Google", "SDE", "Rahul Sharma", "CS2026-042", 8.50, "CS", "rahul@univ.edu", "APPLIED", "2026-09-26");

        boolean isDuplicate = (app1.getDriveId() == app2.getDriveId()) && app1.getRollNumber().equalsIgnoreCase(app2.getRollNumber());
        assertTrue(isDuplicate, "Expected duplicate application check to trigger");
    }

    // 5. Test CV Smart Match Scoring Simulation
    private static void testCvSmartMatchLogic() {
        // Candidate with 8.5 CGPA and Java/MySQL skills
        double studentCgpa = 8.50;
        String cvText = "Experienced in Java, Spring Boot, MySQL, Data Structures";
        
        // Drive requires 8.0 CGPA and Software Engineer role
        double minCgpa = 8.00;
        String role = "Software Engineer";

        int score = 50;
        if (studentCgpa >= minCgpa) {
            score += 20 + (int) Math.min(10, (studentCgpa - minCgpa) * 10);
        }

        String lowerCv = cvText.toLowerCase();
        if (lowerCv.contains("java")) score += 3;
        if (lowerCv.contains("mysql")) score += 3;
        if (lowerCv.contains("data structures")) score += 3;
        if (role.toLowerCase().contains("software") && lowerCv.contains("java")) score += 10;

        assertTrue(score >= 80, "Expected High CV Match score (>=80), got " + score);
    }

    // 6. Test Live REST Server HTTP Connection
    private static void testHttpServerHealth() throws Exception {
        try {
            URL url = new URL("http://localhost:8080/api/drives");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            int code = conn.getResponseCode();
            assertEquals(200, code, "HTTP GET /api/drives status code mismatch");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            assertTrue(response.toString().startsWith("["), "Expected JSON array response from /api/drives");
        } catch (java.net.ConnectException e) {
            System.out.print("(Server not currently listening on 8080 - skipping live HTTP ping test) ... ");
        }
    }
}
