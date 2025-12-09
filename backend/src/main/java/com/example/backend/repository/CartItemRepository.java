package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
