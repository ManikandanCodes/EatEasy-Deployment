package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.MenuCategory;
import com.example.backend.model.MenuItem;
import com.example.backend.repository.MenuCategoryRepository;
import com.example.backend.repository.MenuItemRepository;

@Service
public class MenuItemService {

    private final MenuItemRepository itemRepo;
    private final MenuCategoryRepository categoryRepo;

    public MenuItemService(MenuItemRepository itemRepo, MenuCategoryRepository categoryRepo) {
        this.itemRepo = itemRepo;
        this.categoryRepo = categoryRepo;
    }

    public MenuItem addItem(Long categoryId, MenuItem item) {
        MenuCategory category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        item.setCategory(category);
        return itemRepo.save(item);
    }

    public List<MenuItem> getItemsByCategory(Long categoryId) {
        MenuCategory category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return itemRepo.findByCategory(category);
    }

    public List<MenuItem> getAllItems() {
        return itemRepo.findAll();
    }

    public List<MenuItem> getItemsByRestaurant(Long restaurantId) {
        return itemRepo.findByRestaurantId(restaurantId);
    }

    public MenuItem updateItem(Long itemId, MenuItem newData) {
        MenuItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(newData.getName());
        item.setDescription(newData.getDescription());
        item.setIngredients(newData.getIngredients());
        item.setImageUrl(newData.getImageUrl());
        item.setPrice(newData.getPrice());
        item.setVeg(newData.isVeg());
        item.setBestSeller(newData.isBestSeller());
        item.setOutOfStock(newData.isOutOfStock());

        return itemRepo.save(item);
    }

    public void deleteItem(Long itemId) {
        itemRepo.deleteById(itemId);
    }
}
