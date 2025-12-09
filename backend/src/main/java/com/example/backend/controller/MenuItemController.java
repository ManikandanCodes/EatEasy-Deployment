package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.MenuItemRequest;
import com.example.backend.model.MenuItem;
import com.example.backend.service.MenuItemService;

@RestController
@RequestMapping("/api/menu-items")
@CrossOrigin("*")
public class MenuItemController {

    private final MenuItemService itemService;

    public MenuItemController(MenuItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public MenuItem addItem(@RequestBody MenuItemRequest req) {
        MenuItem item = new MenuItem();

        item.setName(req.getName());
        item.setDescription(req.getDescription());
        item.setIngredients(req.getIngredients());
        item.setImageUrl(req.getImageUrl());
        item.setPrice(req.getPrice());
        item.setVeg(req.isVeg());
        item.setBestSeller(req.isBestSeller());
        item.setOutOfStock(req.isOutOfStock());

        return itemService.addItem(req.getCategoryId(), item);
    }

    @GetMapping
    public List<MenuItem> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/category/{categoryId}")
    public List<MenuItem> getByCategory(@PathVariable Long categoryId) {
        return itemService.getItemsByCategory(categoryId);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<MenuItem> getByRestaurant(@PathVariable Long restaurantId) {
        return itemService.getItemsByRestaurant(restaurantId);
    }

    @PutMapping("/{id}")
    public MenuItem update(@PathVariable Long id, @RequestBody MenuItem item) {
        return itemService.updateItem(id, item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
