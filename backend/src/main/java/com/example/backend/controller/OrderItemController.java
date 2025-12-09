package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.OrderItemRequest;
import com.example.backend.model.OrderItem;
import com.example.backend.service.OrderItemService;

@RestController
@RequestMapping("/api/order-items")
@CrossOrigin("*")
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    public OrderItem addItem(@RequestBody OrderItemRequest req) {
        return orderItemService.addItemToOrder(
                req.getOrderId(),
                req.getMenuItemId(),
                req.getQuantity()
        );
    }

    @GetMapping("/order/{orderId}")
    public List<OrderItem> getByOrder(@PathVariable Long orderId) {
        return orderItemService.getItemsForOrder(orderId);
    }
}
