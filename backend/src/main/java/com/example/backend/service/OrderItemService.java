package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.MenuItem;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.OrderItemRepository;
import com.example.backend.repository.OrderRepository;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepo;
    private final OrderRepository orderRepo;
    private final MenuItemRepository menuItemRepo;

    public OrderItemService(OrderItemRepository orderItemRepo,
                            OrderRepository orderRepo,
                            MenuItemRepository menuItemRepo) {
        this.orderItemRepo = orderItemRepo;
        this.orderRepo = orderRepo;
        this.menuItemRepo = menuItemRepo;
    }

    public OrderItem addItemToOrder(Long orderId, Long menuItemId, int quantity) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        MenuItem menuItem = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(menuItem.getPrice() * quantity);

        return orderItemRepo.save(orderItem);
    }

    public List<OrderItem> getItemsForOrder(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return orderItemRepo.findByOrder(order);
    }
}
