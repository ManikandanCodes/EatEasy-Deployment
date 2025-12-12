package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.model.User;
import com.example.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setPassword(req.getPassword());
        user.setRole(User.Role.valueOf(req.getRole()));

        if (req.getRole().equalsIgnoreCase("ROLE_RESTAURANT_OWNER")
                || req.getRole().equalsIgnoreCase("RESTAURANT_OWNER")) {
            user.setRestaurantRegistered(false);
        }

        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse response = authService.login(req.getEmail(), req.getPassword());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        try {
            authService.forgotPassword(email);
            return ResponseEntity.ok(java.util.Collections.singletonMap("message", "OTP sent to your email"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        String newPassword = body.get("newPassword");
        try {
            authService.resetPassword(email, otp, newPassword);
            return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Password reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        String email = authentication.getName();

        return ResponseEntity.ok(authService.getUserByEmail(email));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        String otp = body.get("otp");
        try {
            authService.verifyOtp(email, otp);
            return ResponseEntity.ok(java.util.Collections.singletonMap("message", "OTP verified successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        try {
            authService.resendOtp(email);
            return ResponseEntity.ok(java.util.Collections.singletonMap("message", "OTP resent successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(java.util.Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/test-email")
    public ResponseEntity<?> testEmail(@RequestBody java.util.Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        boolean sent = authService.sendTestEmail(email);
        if (sent) {
            return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Test email sent successfully"));
        } else {
            return ResponseEntity.status(500)
                    .body(java.util.Collections.singletonMap("error", "Failed to send email. Check server logs."));
        }
    }
}
