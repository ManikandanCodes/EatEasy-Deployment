package com.example.backend.dto;

import com.example.backend.model.User;

public class LoginResponse {

    private String accessToken;
    private String role;
    private UserInfo user;

    private boolean isRestaurantRegistered;

    public LoginResponse() {
    }

    public LoginResponse(String accessToken, User user, boolean isRestaurantRegistered) {
        this.accessToken = accessToken;
        this.role = user.getRole().name();
        this.user = new UserInfo(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
        this.isRestaurantRegistered = isRestaurantRegistered;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public boolean isRestaurantRegistered() {
        return isRestaurantRegistered;
    }

    public void setRestaurantRegistered(boolean restaurantRegistered) {
        isRestaurantRegistered = restaurantRegistered;
    }

    public static class UserInfo {
        private Long id;
        private String name;
        private String email;
        private String role;

        public UserInfo(Long id, String name, String email, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}
