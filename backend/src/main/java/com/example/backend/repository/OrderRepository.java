package com.example.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Order;
import com.example.backend.model.Restaurant;
import com.example.backend.model.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomer(User customer);

    List<Order> findByRestaurant(Restaurant restaurant);

    List<Order> findByStatus(String status);
}
