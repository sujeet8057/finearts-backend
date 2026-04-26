package com.college.LNCT.controller;

import com.college.LNCT.dto.AdminLoginRequest;
import com.college.LNCT.dto.AdminLoginResponse;
import com.college.LNCT.dto.AdminSignupRequest;
import com.college.LNCT.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Admin signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AdminSignupRequest request) {
        log.info("Admin signup attempt: {}", request.getEmail());

        try {
            AdminLoginResponse response = adminService.signup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("Signup failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during signup", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during signup");
        }
    }

    // Admin login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest request) {
        log.info("Admin login attempt: {}", request.getEmail());

        try {
            AdminLoginResponse response = adminService.login(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    // Test endpoint (for verification)
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin API is working");
    }
}
