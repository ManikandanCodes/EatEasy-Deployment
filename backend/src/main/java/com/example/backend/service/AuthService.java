package com.example.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.config.JwtUtil;
import com.example.backend.dto.LoginResponse;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final com.example.backend.repository.RestaurantRepository restaurantRepo;
    private final EmailService emailService;

    public AuthService(UserRepository userRepo, JwtUtil jwtUtil,
            com.example.backend.repository.RestaurantRepository restaurantRepo,
            EmailService emailService) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.restaurantRepo = restaurantRepo;
        this.emailService = emailService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User register(User newUser) {

        java.util.Optional<User> existingUserOpt = userRepo.findByEmail(newUser.getEmail());

        User userToSave;

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (Boolean.TRUE.equals(existingUser.getActive())) {
                throw new RuntimeException("Email already registered");
            }
            // User exists but is not active (unverified). Update details and resend OTP.
            userToSave = existingUser;
            userToSave.setName(newUser.getName());
            userToSave.setPhone(newUser.getPhone());
            userToSave.setPassword(passwordEncoder.encode(newUser.getPassword()));
            userToSave.setRole(newUser.getRole());
            if (newUser.getRole() == User.Role.RESTAURANT_OWNER) {
                userToSave.setRestaurantRegistered(false);
            }
        } else {
            // New User
            userToSave = newUser;
            userToSave.setPassword(passwordEncoder.encode(newUser.getPassword()));
            if (userToSave.getRole() == User.Role.RESTAURANT_OWNER) {
                userToSave.setRestaurantRegistered(false);
            }
            userToSave.setActive(false);
        }

        // Generate OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        userToSave.setOtp(otp);
        userToSave.setOtpExpiryTime(java.time.LocalDateTime.now().plusMinutes(10));

        User savedUser = userRepo.save(userToSave);

        // Send Email
        // Send Email
        boolean emailSent = emailService.sendEmail(savedUser.getEmail(), "EatEasy Registration OTP",
                "Your OTP for registration is: " + otp + ". It expires in 10 minutes.");

        if (!emailSent) {
            throw new RuntimeException("Failed to send OTP email. Please check the email address or try again later.");
        }

        return savedUser;
    }

    public boolean verifyOtp(String email, String otp) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (Boolean.TRUE.equals(user.getActive())) {
            return true; // Already verified
        }

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired");
        }

        // OTP Valid
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        user.setActive(true);
        userRepo.save(user);
        return true;
    }

    public void resendOtp(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Removed check for active status to allow resend for both register and forgot
        // password flows effectively,
        // or keep strictness? For forgot password, user IS active usually.
        // Let's create a separate logic or reuse carefully.
        // Actually, resendOtp is for registration typically.
        // Let's rely on forgotPassword methods.

        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        user.setOtp(otp);
        user.setOtpExpiryTime(java.time.LocalDateTime.now().plusMinutes(10));
        userRepo.save(user);

        boolean emailSent = emailService.sendEmail(user.getEmail(), "EatEasy Registration OTP (Resend)",
                "Your new OTP for registration is: " + otp + ". It expires in 10 minutes.");

        if (!emailSent) {
            throw new RuntimeException("Failed to resend OTP email.");
        }
    }

    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate OTP
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        user.setOtp(otp);
        user.setOtpExpiryTime(java.time.LocalDateTime.now().plusMinutes(10));
        userRepo.save(user);

        boolean emailSent = emailService.sendEmail(user.getEmail(), "EatEasy Reset Password OTP",
                "Your OTP to reset password is: " + otp + ". It expires in 10 minutes.");

        if (!emailSent) {
            throw new RuntimeException("Failed to send password reset OTP.");
        }
    }

    public void resetPassword(String email, String otp, String newPassword) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        if (user.getOtpExpiryTime().isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("OTP Expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        // Ensure user is active after reset (in case they were stuck in verification?)
        // Typically forgot password assumes user was already registered.
        // If they verify here, they are definitely active.
        user.setActive(true);
        userRepo.save(user);
    }

    public LoginResponse login(String email, String password) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getActive() != null && !user.getActive()) {
            if (user.getOtp() != null) {
                throw new RuntimeException("Account not verified. Please verify your OTP.");
            }
            throw new RuntimeException("Your account has been blocked. Please contact admin.");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        if (!user.isRestaurantRegistered() && user.getRole() == User.Role.RESTAURANT_OWNER) {
            boolean hasRestaurant = !restaurantRepo.findByOwnerId(user.getId()).isEmpty();
            if (hasRestaurant) {
                user.setRestaurantRegistered(true);
                userRepo.save(user);
            }
        }

        boolean isRegistered = user.isRestaurantRegistered();

        return new LoginResponse(token, user, isRegistered);
    }

    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean sendTestEmail(String email) {
        return emailService.sendEmail(email, "EatEasy Test Email",
                "This is a test email to verify your configuration.");
    }
}
