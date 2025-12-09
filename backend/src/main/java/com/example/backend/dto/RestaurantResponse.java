package com.example.backend.dto;

public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String cuisines;
    private double rating;
    private String openingHours;
    private boolean open;

    public RestaurantResponse(Long id, String name, String address,
                              String cuisines, double rating,
                              String openingHours, boolean open) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.cuisines = cuisines;
        this.rating = rating;
        this.openingHours = openingHours;
        this.open = open;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCuisines() { return cuisines; }
    public double getRating() { return rating; }
    public String getOpeningHours() { return openingHours; }
    public boolean isOpen() { return open; }
}
