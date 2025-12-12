package com.example.backend.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.Order;
import com.example.backend.model.Restaurant;
import com.example.backend.model.User;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.RestaurantRepository;
import com.example.backend.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final RestaurantRepository restaurantRepo;

    public OrderService(OrderRepository orderRepo, UserRepository userRepo,
            RestaurantRepository restaurantRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.restaurantRepo = restaurantRepo;
    }

    public Order placeOrder(Long userId, Long restaurantId, Order order) {

        User customer = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setOrderTime(new Date());
        order.setStatus("PLACED");

        return orderRepo.save(order);
    }

    public List<Order> getOrdersForCustomer(Long userId) {
        User customer = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return orderRepo.findByCustomer(customer);
    }

    public List<Order> getOrdersForRestaurant(Long restaurantId) {
        Restaurant restaurant = restaurantRepo.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        return orderRepo.findByRestaurant(restaurant);
    }

    public Order updateStatus(Long orderId, String status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepo.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public void deleteAllOrders() {
        orderRepo.deleteAll();
    }

    public Order rateOrder(Long orderId, Integer rating, String reviewComment) {
        Order order = getOrderById(orderId);
        order.setRating(rating);
        order.setReviewComment(reviewComment);
        return orderRepo.save(order);
    }
}
