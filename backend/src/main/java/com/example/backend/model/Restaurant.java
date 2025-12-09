package com.example.backend.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "restaurants")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String cuisines; 
    private double rating;
    private String openingHours;
    private boolean open;


    private String phone;
    private String description;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus status = ApprovalStatus.PENDING; 

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;


    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<MenuCategory> categories;

    
    public enum ApprovalStatus {
        PENDING, 
        APPROVED, 
        REJECTED 
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCuisines() {
        return cuisines;
    }

    public double getRating() {
        return rating;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public boolean isOpen() {
        return open;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public User getOwner() {
        return owner;
    }

    public List<MenuCategory> getCategories() {
        return categories;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCuisines(String cuisines) {
        this.cuisines = cuisines;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setCategories(List<MenuCategory> categories) {
        this.categories = categories;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    
    @jakarta.persistence.Transient
    public boolean isAvailable() {
        if (!open) {
            return false; 
        }
        if (openingHours == null || openingHours.isEmpty()) {
            return open; 
        }

        try {

            String[] parts = openingHours.split("-");
            if (parts.length != 2)
                return open;

            
            java.time.ZoneId zone = java.time.ZoneId.of("Asia/Kolkata");
            java.time.LocalTime now = java.time.LocalTime.now(zone);

            java.time.LocalTime start = parseTime(parts[0].trim());
            java.time.LocalTime end = parseTime(parts[1].trim());

            if (start == null || end == null)
                return open;

            
            if (end.isBefore(start)) {
                return now.isAfter(start) || now.isBefore(end);
            } else {
                return now.isAfter(start) && now.isBefore(end);
            }
        } catch (Exception e) {
            System.err.println("Error checking availability: " + e.getMessage());
            return open;
        }
    }

    private java.time.LocalTime parseTime(String timeStr) {
        try {
            timeStr = timeStr.trim().toUpperCase();
    
            timeStr = timeStr.replaceAll("\\s+", " ");

            java.time.format.DateTimeFormatter formatter;
            if (timeStr.contains("AM") || timeStr.contains("PM")) {
                
                if (!timeStr.contains(":")) {
                    timeStr = timeStr.replace(" AM", ":00 AM").replace(" PM", ":00 PM");
                }
                formatter = java.time.format.DateTimeFormatter.ofPattern("h:mm a");
            } else {
                formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm");
            }
            return java.time.LocalTime.parse(timeStr, formatter);
        } catch (Exception e) {
            System.err.println("Error parsing time: " + timeStr);
            return null;
        }
    }
}
