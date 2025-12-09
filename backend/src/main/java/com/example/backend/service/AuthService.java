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

    public AuthService(UserRepository userRepo, JwtUtil jwtUtil,
            com.example.backend.repository.RestaurantRepository restaurantRepo) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.restaurantRepo = restaurantRepo;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User register(User user) {

        if (userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered");
        }


        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == User.Role.RESTAURANT_OWNER) {
            user.setRestaurantRegistered(false);
        }

        return userRepo.save(user);
    }


    public LoginResponse login(String email, String password) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }


        if (user.getActive() != null && !user.getActive()) {
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
}
