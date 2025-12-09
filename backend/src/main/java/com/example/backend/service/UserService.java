package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

   
    public User updateUser(Long id, User newData) {
        User user = getUserById(id);

        user.setName(newData.getName());
        user.setPhone(newData.getPhone());

        return userRepo.save(user);
    }

 
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepo.deleteById(id);
    }


    public List<User> getAllCustomers() {
        return userRepo.findAll()
                .stream()
                .filter(u -> u.getRole() == User.Role.CUSTOMER)
                .toList();
    }


    public List<User> getAllRestaurantOwners() {
        return userRepo.findAll()
                .stream()
                .filter(u -> u.getRole() == User.Role.RESTAURANT_OWNER)
                .toList();
    }
}
