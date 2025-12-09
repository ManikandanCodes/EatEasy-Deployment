package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.Restaurant;
import com.example.backend.repository.RestaurantRepository;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepo;
    private final com.example.backend.repository.UserRepository userRepo;

    public RestaurantService(RestaurantRepository restaurantRepo,
            com.example.backend.repository.UserRepository userRepo) {
        this.restaurantRepo = restaurantRepo;
        this.userRepo = userRepo;
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        Restaurant saved = restaurantRepo.save(restaurant);

  
        com.example.backend.model.User owner = saved.getOwner();
        if (owner != null) {
            owner.setRestaurantRegistered(true);
            userRepo.save(owner);
        }

        return saved;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepo.findAll();
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public List<Restaurant> searchByName(String name) {
        return restaurantRepo.findByNameContainingIgnoreCase(name);
    }

    public List<Restaurant> searchByCuisine(String cuisine) {
        return restaurantRepo.findByCuisinesContainingIgnoreCase(cuisine);
    }

    public List<Restaurant> getOpenRestaurants() {
        return restaurantRepo.findByOpen(true);
    }

    public Restaurant updateRestaurant(Long id, Restaurant updated) {
        Restaurant restaurant = getRestaurantById(id);

        restaurant.setName(updated.getName());
        restaurant.setAddress(updated.getAddress());
        restaurant.setCuisines(updated.getCuisines());
        restaurant.setRating(updated.getRating());
        restaurant.setOpeningHours(updated.getOpeningHours());
        restaurant.setOpen(updated.isOpen());
        restaurant.setPhone(updated.getPhone());
        restaurant.setDescription(updated.getDescription());
        restaurant.setImageUrl(updated.getImageUrl());

        return restaurantRepo.save(restaurant);
    }

    public void deleteRestaurant(Long id) {
        restaurantRepo.deleteById(id);
    }

    public Restaurant updateStatus(Long id, boolean isOpen) {
        Restaurant restaurant = getRestaurantById(id);
        restaurant.setOpen(isOpen);
        return restaurantRepo.save(restaurant);
    }

    public List<Restaurant> searchRestaurants(String search, String location, String cuisine) {
        return restaurantRepo.findAll().stream()
                .filter(r -> r.getStatus() == Restaurant.ApprovalStatus.APPROVED) // Only show approved restaurants
                .filter(r -> search == null || search.isEmpty()
                        || r.getName().toLowerCase().contains(search.toLowerCase()))
                .filter(r -> location == null || location.isEmpty()
                        || r.getAddress().toLowerCase().contains(location.toLowerCase()))
                .filter(r -> cuisine == null || cuisine.isEmpty()
                        || (r.getCuisines() != null && r.getCuisines().toLowerCase().contains(cuisine.toLowerCase())))
                .collect(java.util.stream.Collectors.toList());
    }
}
