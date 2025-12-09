package com.example.backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Order;
import com.example.backend.model.Restaurant;
import com.example.backend.model.User;
import com.example.backend.repository.RestaurantRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.OrderService;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin("*")
public class AnalyticsController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public AnalyticsController(OrderService orderService, UserRepository userRepository,
            RestaurantRepository restaurantRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/restaurant")
    public Map<String, Object> getRestaurantAnalytics(Authentication authentication) {

        User owner;
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            owner = (User) principal;
        } else {
            String username = authentication.getName();
            owner = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Restaurant restaurant = restaurantRepository.findByOwnerId(owner.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Restaurant not found for this owner"));

        List<Order> orders = orderService.getOrdersForRestaurant(restaurant.getId());

        Map<String, Object> analytics = new HashMap<>();

        int totalOrders = orders.size();
        int acceptedOrders = 0;
        int rejectedOrders = 0;
        int completedOrders = 0;
        double totalRevenue = 0.0;

        for (Order order : orders) {
            String status = order.getStatus();

            if ("ACCEPTED".equals(status) || "PREPARING".equals(status) ||
                    "READY".equals(status) || "OUT_FOR_DELIVERY".equals(status) ||
                    "COMPLETED".equals(status) || "DELIVERED".equals(status)) {
                acceptedOrders++;
            }

            if ("REJECTED".equals(status) || "CANCELLED".equals(status)) {
                rejectedOrders++;
            }

            if ("DELIVERED".equals(status) || "COMPLETED".equals(status)) {
                completedOrders++;
            }

            Double price = order.getTotalPrice();
            if (price == null || price == 0.0) {
                price = order.getItems().stream()
                        .mapToDouble(i -> i.getPrice() * i.getQuantity())
                        .sum();
            }

            if (price > 0 &&
                    !"REJECTED".equals(status) && !"CANCELLED".equals(status)) {
                totalRevenue += price;
            }
        }

        analytics.put("totalOrders", totalOrders);
        analytics.put("acceptedOrders", acceptedOrders);
        analytics.put("rejectedOrders", rejectedOrders);
        analytics.put("completedOrders", completedOrders);
        analytics.put("totalRevenue", totalRevenue);

        return analytics;
    }

}
