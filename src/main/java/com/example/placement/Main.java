package com.example.placement;

import com.example.placement.model.PlacementDrive;
import com.example.placement.model.Student;
import com.example.placement.model.StudentApplication;
import com.example.placement.model.User;
import com.example.placement.service.PlacementService;
import com.example.placement.service.impl.PlacementServiceImpl;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main application entry point for Campus Placement Portal.
 * Handles Auth, Student Profiles, Drives, and Applications REST API endpoints.
 */
public class Main {
    private static final int PORT = 8080;
    
    // POLYMORPHISM: Interface reference pointing to concrete implementation
    private static final PlacementService placementService = new PlacementServiceImpl();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // REST API Handlers
        server.createContext("/api/auth/register", new AuthRegisterHandler());
        server.createContext("/api/auth/login", new AuthLoginHandler());
        server.createContext("/api/drives", new DrivesApiHandler());
        server.createContext("/api/applications", new ApplicationsApiHandler());
        server.createContext("/api/students", new StudentsApiHandler());

        // Static File Handler for Frontend HTML/CSS/JS
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("=================================================");
        System.out.println(" 🎓 Pure OOP Java Backend + JDBC Server Started!");
        System.out.println(" Web Portal: http://localhost:" + PORT);
        System.out.println(" Auth API: http://localhost:" + PORT + "/api/auth/login");
        System.out.println("=================================================");
    }

    /**
     * User Registration Handler (/api/auth/register)
     */
    static class AuthRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String body = readRequestBody(exchange);
                User newUser = User.parseJson(body);

                if (newUser.getUsername() == null || newUser.getUsername().trim().isEmpty() ||
                    newUser.getPassword() == null || newUser.getPassword().trim().isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Username and password are required\"}");
                    return;
                }

                User registered = placementService.registerUser(newUser);
                sendJsonResponse(exchange, 201, registered.toJson());
            } catch (IllegalArgumentException e) {
                sendJsonResponse(exchange, 400, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            } catch (SQLException e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"Database error: " + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * User Login Handler (/api/auth/login)
     */
    static class AuthLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String body = readRequestBody(exchange);
                User creds = User.parseJson(body);

                if (creds.getUsername() == null || creds.getPassword() == null) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Username and password required\"}");
                    return;
                }

                User user = placementService.loginUser(creds.getUsername(), creds.getPassword());
                if (user != null) {
                    sendJsonResponse(exchange, 200, user.toJson());
                } else {
                    sendJsonResponse(exchange, 401, "{\"error\":\"Invalid username or password\"}");
                }
            } catch (SQLException e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"Database error: " + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    /**
     * Student Profile API Handler (/api/students)
     */
    static class StudentsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();

            try {
                if ("GET".equalsIgnoreCase(method)) {
                    if (query != null && query.contains("rollNumber=")) {
                        String roll = query.split("rollNumber=")[1].split("&")[0];
                        Student student = placementService.getStudentByRoll(roll);
                        if (student != null) {
                            sendJsonResponse(exchange, 200, student.toJson());
                        } else {
                            sendJsonResponse(exchange, 404, "{\"error\":\"Student profile not found\"}");
                        }
                    } else {
                        List<Student> students = placementService.getAllStudents();
                        String json = "[" + students.stream().map(Student::toJson).collect(Collectors.joining(",")) + "]";
                        sendJsonResponse(exchange, 200, json);
                    }
                } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                    String body = readRequestBody(exchange);
                    Student s = Student.parseJson(body);

                    if (s.getRollNumber() == null || s.getRollNumber().trim().isEmpty() ||
                        s.getStudentName() == null || s.getStudentName().trim().isEmpty()) {
                        sendJsonResponse(exchange, 400, "{\"error\":\"Roll Number and Student Name are required\"}");
                        return;
                    }

                    Student saved = placementService.saveStudentProfile(s);
                    sendJsonResponse(exchange, 200, saved.toJson());
                } else {
                    sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
                }
            } catch (SQLException e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"Database error: " + escapeJson(e.getMessage()) + "\"}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"Server error\"}");
            }
        }
    }

    /**
     * Placement Drives API Handler (/api/drives)
     */
    static class DrivesApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            try {
                if ("GET".equalsIgnoreCase(method)) {
                    if (parts.length <= 3) {
                        List<PlacementDrive> drives = placementService.getAllDrives();
                        String json = "[" + drives.stream().map(PlacementDrive::toJson).collect(Collectors.joining(",")) + "]";
                        sendJsonResponse(exchange, 200, json);
                    } else {
                        int id = Integer.parseInt(parts[3]);
                        PlacementDrive drive = placementService.getDriveById(id);
                        if (drive != null) {
                            sendJsonResponse(exchange, 200, drive.toJson());
                        } else {
                            sendJsonResponse(exchange, 404, "{\"error\":\"Drive not found\"}");
                        }
                    }
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = readRequestBody(exchange);
                    PlacementDrive newDrive = PlacementDrive.parseJson(body);

                    if (newDrive.getCompanyName() == null || newDrive.getCompanyName().trim().isEmpty()) {
                        sendJsonResponse(exchange, 400, "{\"error\":\"Company Name is required\"}");
                        return;
                    }

                    PlacementDrive created = placementService.createDrive(newDrive);
                    sendJsonResponse(exchange, 201, created.toJson());
                } else if ("PUT".equalsIgnoreCase(method)) {
                    if (parts.length > 3) {
                        int id = Integer.parseInt(parts[3]);
                        String body = readRequestBody(exchange);
                        PlacementDrive driveUpdate = PlacementDrive.parseJson(body);

                        boolean updated = false;
                        if (driveUpdate.getCompanyName() != null && !driveUpdate.getCompanyName().isEmpty()) {
                            updated = placementService.updateDrive(id, driveUpdate);
                        } else if (driveUpdate.getStatus() != null) {
                            updated = placementService.updateDriveStatus(id, driveUpdate.getStatus());
                        }

                        if (updated) {
                            sendJsonResponse(exchange, 200, "{\"message\":\"Drive updated successfully\"}");
                        } else {
                            sendJsonResponse(exchange, 404, "{\"error\":\"Drive update failed\"}");
                        }
                    }
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    if (parts.length > 3) {
                        int id = Integer.parseInt(parts[3]);
                        boolean deleted = placementService.deleteDrive(id);
                        if (deleted) {
                            sendJsonResponse(exchange, 200, "{\"message\":\"Drive deleted successfully\"}");
                        } else {
                            sendJsonResponse(exchange, 404, "{\"error\":\"Drive not found\"}");
                        }
                    }
                }
            } catch (SQLException e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"Database error: " + escapeJson(e.getMessage()) + "\"}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"Server error\"}");
            }
        }
    }

    /**
     * Student Applications API Handler (/api/applications)
     */
    static class ApplicationsApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            String method = exchange.getRequestMethod();

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");

            try {
                if ("GET".equalsIgnoreCase(method)) {
                    List<StudentApplication> apps;
                    if (query != null && query.contains("rollNumber=")) {
                        String roll = query.split("rollNumber=")[1].split("&")[0];
                        apps = placementService.getApplicationsByRoll(roll);
                    } else {
                        apps = placementService.getAllApplications();
                    }
                    String json = "[" + apps.stream().map(StudentApplication::toJson).collect(Collectors.joining(",")) + "]";
                    sendJsonResponse(exchange, 200, json);
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = readRequestBody(exchange);
                    StudentApplication newApp = StudentApplication.parseJson(body);

                    if (newApp.getDriveId() <= 0 || newApp.getStudentName() == null || newApp.getRollNumber() == null) {
                        sendJsonResponse(exchange, 400, "{\"error\":\"Drive ID, Student Name, and Roll Number are required\"}");
                        return;
                    }

                    StudentApplication created = placementService.applyForDrive(newApp);
                    sendJsonResponse(exchange, 201, created.toJson());
                } else if ("PUT".equalsIgnoreCase(method)) {
                    if (parts.length > 3) {
                        int id = Integer.parseInt(parts[3]);
                        String body = readRequestBody(exchange);
                        StudentApplication updateApp = StudentApplication.parseJson(body);

                        if (updateApp.getApplicationStatus() != null) {
                            boolean updated = placementService.updateApplicationStatus(id, updateApp.getApplicationStatus());
                            if (updated) {
                                sendJsonResponse(exchange, 200, "{\"message\":\"Application status updated\"}");
                                return;
                            }
                        }
                        sendJsonResponse(exchange, 400, "{\"error\":\"Update failed\"}");
                    }
                } else if ("DELETE".equalsIgnoreCase(method)) {
                    if (parts.length > 3) {
                        int id = Integer.parseInt(parts[3]);
                        boolean deleted = placementService.deleteApplication(id);
                        if (deleted) {
                            sendJsonResponse(exchange, 200, "{\"message\":\"Application deleted\"}");
                        } else {
                            sendJsonResponse(exchange, 404, "{\"error\":\"Application not found\"}");
                        }
                    }
                }
            } catch (SQLException e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"Database error: " + escapeJson(e.getMessage()) + "\"}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"Server error\"}");
            }
        }
    }

    /**
     * Static Asset Handler
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";

            Path filePath = Paths.get("src/main/resources/public" + path);

            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                String res = "404 - Not Found";
                exchange.sendResponseHeaders(404, res.length());
                try (OutputStream os = exchange.getResponseBody()) { os.write(res.getBytes()); }
                return;
            }

            String contentType = "text/plain";
            if (path.endsWith(".html")) contentType = "text/html; charset=UTF-8";
            else if (path.endsWith(".css")) contentType = "text/css; charset=UTF-8";
            else if (path.endsWith(".js")) contentType = "application/javascript; charset=UTF-8";

            exchange.getResponseHeaders().set("Content-Type", contentType);
            byte[] bytes = Files.readAllBytes(filePath);
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
        }
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("X-Content-Type-Options", "nosniff");
        exchange.getResponseHeaders().add("X-Frame-Options", "DENY");
        exchange.getResponseHeaders().add("X-XSS-Protection", "1; mode=block");
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\"", "\\\"");
    }
}
