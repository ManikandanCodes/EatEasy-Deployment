package com.example.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.OrderRequest;
import com.example.backend.model.MenuItem;
import com.example.backend.model.Order;
import com.example.backend.model.OrderItem;
import com.example.backend.model.Restaurant;
import com.example.backend.repository.MenuItemRepository;
import com.example.backend.repository.RestaurantRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.OrderService;
import com.example.backend.repository.CouponRepository;
import com.example.backend.model.Coupon;
import org.springframework.security.core.Authentication;
import com.example.backend.model.User;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final CouponRepository couponRepository;

    public OrderController(OrderService orderService, UserRepository userRepository,
            RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository,
            CouponRepository couponRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.couponRepository = couponRepository;
    }

    @PostMapping
    public Order placeOrder(@RequestBody OrderRequest req) {
        Order order = new Order();
        order.setTotalPrice(req.getTotalAmount());
        order.setDeliveryAddress(req.getDeliveryAddress());
        order.setPhone(req.getPhone());

        List<OrderItem> orderItems = new ArrayList<>();
        if (req.getItems() != null) {
            for (OrderRequest.OrderItemRequest itemReq : req.getItems()) {
                OrderItem item = new OrderItem();
                item.setQuantity(itemReq.getQuantity());
                item.setPrice(itemReq.getPrice());
                item.setOrder(order);

                MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException("Menu Item not found"));
                item.setMenuItem(menuItem);

                orderItems.add(item);
            }
        }
        order.setItems(orderItems);

        return orderService.placeOrder(req.getUserId(), req.getRestaurantId(), order);
    }

    @GetMapping("/restaurant")
    public List<Order> getOrdersForCurrentRestaurant(Authentication authentication) {

        if (authentication == null) {
            return List.of();
        }

        User owner;
        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            owner = (User) principal;
        } else {
            String username = authentication.getName();
            owner = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(owner.getId());

        if (restaurants.isEmpty()) {
            return List.of();
        }

        Restaurant restaurant = restaurants.get(0);

        return orderService.getOrdersForRestaurant(restaurant.getId());
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/customer/{userId}")
    public List<Order> getOrdersForCustomer(@PathVariable Long userId) {
        return orderService.getOrdersForCustomer(userId);
    }

    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }

    @PostMapping("/validate-coupon")
    public Coupon validateCoupon(@RequestBody java.util.Map<String, Object> body) {
        String code = (String) body.get("code");
        Double amount = Double.valueOf(body.get("amount").toString());

        return couponRepository.findByCode(code)
                .filter(c -> c.getExpiryDate().isAfter(java.time.LocalDate.now()))
                .filter(c -> amount >= c.getMinPurchaseAmount())
                .orElseThrow(() -> new RuntimeException("Invalid coupon, expired, or minimum purchase amount not met"));
    }

    @GetMapping("/coupons")
    public List<Coupon> getActiveCoupons() {
        return couponRepository.findAll().stream()
                .filter(c -> c.getExpiryDate().isAfter(java.time.LocalDate.now()))
                .collect(java.util.stream.Collectors.toList());
    }

    @PostMapping("/{id}/rate")
    public Order rateOrder(@PathVariable Long id, @RequestBody java.util.Map<String, Object> body) {
        Integer rating = (Integer) body.get("rating");
        String comment = (String) body.get("comment");
        return orderService.rateOrder(id, rating, comment);
    }

    @DeleteMapping("/all")
    public java.util.Map<String, String> deleteAllOrders() {
        orderService.deleteAllOrders();
        return java.util.Map.of("message", "All orders have been deleted successfully");
    }
}
