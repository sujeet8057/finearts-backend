package com.college.LNCT.service;

import com.college.LNCT.dto.AdminLoginRequest;
import com.college.LNCT.dto.AdminLoginResponse;
import com.college.LNCT.dto.AdminSignupRequest;
import com.college.LNCT.entity.Admin;
import com.college.LNCT.repository.AdminRepository;
import com.college.LNCT.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Slf4j
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Admin login
    public AdminLoginResponse login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByEmailAndIsActiveTrue(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Update last login
        admin.setLastLogin(LocalDateTime.now());
        adminRepository.save(admin);

        // Generate token
        String token = jwtTokenProvider.generateToken(admin.getEmail(), admin.getName());

        log.info("Admin logged in: {}", admin.getEmail());

        return AdminLoginResponse.builder()
                .token(token)
                .email(admin.getEmail())
                .name(admin.getName())
                .role("ADMIN")
                .build();
    }

    // Get admin by email
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    // Admin signup
    public AdminLoginResponse signup(AdminSignupRequest request) {
        if (adminRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Admin with this email already exists");
        }

        Admin admin = Admin.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .isActive(true)
                .build();

        adminRepository.save(admin);
        log.info("Admin registered: {}", request.getEmail());

        String token = jwtTokenProvider.generateToken(admin.getEmail(), admin.getName());

        return AdminLoginResponse.builder()
                .token(token)
                .email(admin.getEmail())
                .name(admin.getName())
                .role("ADMIN")
                .build();
    }

    // Create admin (for setup)
    public void createAdmin(String email, String password, String name) {
        if (adminRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Admin with this email already exists");
        }

        Admin admin = Admin.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .isActive(true)
                .build();

        adminRepository.save(admin);
        log.info("Admin created: {}", email);
    }
}
