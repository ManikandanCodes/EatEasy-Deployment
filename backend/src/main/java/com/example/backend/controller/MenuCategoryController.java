package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.MenuCategoryRequest;
import com.example.backend.model.MenuCategory;
import com.example.backend.service.MenuCategoryService;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin("*")
public class MenuCategoryController {

    private final MenuCategoryService categoryService;

    public MenuCategoryController(MenuCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public MenuCategory addCategory(@RequestBody MenuCategoryRequest req) {
        MenuCategory c = new MenuCategory();
        c.setName(req.getName());
        return categoryService.createCategory(req.getRestaurantId(), c);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuCategory> getByRestaurant(@PathVariable Long restaurantId) {
        return categoryService.getCategoriesByRestaurant(restaurantId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
