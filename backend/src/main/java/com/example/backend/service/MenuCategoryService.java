package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.MenuCategory;
import com.example.backend.model.Restaurant;
import com.example.backend.repository.MenuCategoryRepository;
import com.example.backend.repository.RestaurantRepository;

@Service
public class MenuCategoryService {

    private final MenuCategoryRepository categoryRepo;
    private final RestaurantRepository restaurantRepo;

    public MenuCategoryService(MenuCategoryRepository categoryRepo, RestaurantRepository restaurantRepo) {
        this.categoryRepo = categoryRepo;
        this.restaurantRepo = restaurantRepo;
    }

    public MenuCategory createCategory(Long restaurantId, MenuCategory category) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        category.setRestaurant(restaurant);
        return categoryRepo.save(category);
    }

    public List<MenuCategory> getCategoriesByRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        return categoryRepo.findByRestaurant(restaurant);
    }

    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }
}
