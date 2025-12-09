package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Restaurant;
import com.example.backend.model.User;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByNameContainingIgnoreCase(String name);

    List<Restaurant> findByCuisinesContainingIgnoreCase(String cuisine);

    List<Restaurant> findByOpen(boolean open);

    List<Restaurant> findByOwnerId(Long ownerId);

    Optional<Restaurant> findByOwner(User owner);
}
