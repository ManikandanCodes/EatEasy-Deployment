package com.example.backend.dto;

import java.util.Date;

public class OrderResponse {

    private Long id;
    private Date orderTime;
    private String status;
    private Long restaurantId;

    public OrderResponse(Long id, Date orderTime, String status, Long restaurantId) {
        this.id = id;
        this.orderTime = orderTime;
        this.status = status;
        this.restaurantId = restaurantId;
    }

    public Long getId() { return id; }
    public Date getOrderTime() { return orderTime; }
    public String getStatus() { return status; }
    public Long getRestaurantId() { return restaurantId; }
}
