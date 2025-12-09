package com.example.backend.dto;

public class OrderItemResponse {

    private Long id;
    private String itemName;
    private int quantity;
    private double price;

    public OrderItemResponse(Long id, String itemName, int quantity, double price) {
        this.id = id;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}
