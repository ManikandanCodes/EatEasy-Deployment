package com.example.backend.dto;

public class MenuItemResponse {

    private Long id;
    private String name;
    private double price;
    private boolean veg;
    private boolean bestSeller;

    public MenuItemResponse(Long id, String name, double price,
                            boolean veg, boolean bestSeller) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.veg = veg;
        this.bestSeller = bestSeller;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean isVeg() { return veg; }
    public boolean isBestSeller() { return bestSeller; }
}
