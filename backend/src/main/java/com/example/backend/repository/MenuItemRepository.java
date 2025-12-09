package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.MenuCategory;
import com.example.backend.model.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategory(MenuCategory category);

    List<MenuItem> findByVeg(boolean veg);

    List<MenuItem> findByBestSeller(boolean bestSeller);

    List<MenuItem> findByOutOfStock(boolean outOfStock);

    @org.springframework.data.jpa.repository.Query("SELECT m FROM MenuItem m WHERE m.category.restaurant.id = :restaurantId")
    List<MenuItem> findByRestaurantId(Long restaurantId);
}
