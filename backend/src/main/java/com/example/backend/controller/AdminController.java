package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.model.Restaurant;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.RestaurantRepository;
import com.example.backend.model.Order;
import com.example.backend.repository.OrderRepository;
import com.example.backend.model.Coupon;
import com.example.backend.repository.CouponRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

        private final UserRepository userRepository;
        private final RestaurantRepository restaurantRepository;
        private final OrderRepository orderRepository;
        private final CouponRepository couponRepository;

        public AdminController(
                        UserRepository userRepository,
                        RestaurantRepository restaurantRepository,
                        OrderRepository orderRepository,
                        CouponRepository couponRepository) {
                this.userRepository = userRepository;
                this.restaurantRepository = restaurantRepository;
                this.orderRepository = orderRepository;
                this.couponRepository = couponRepository;
        }

        
        @GetMapping("/stats")
        public ResponseEntity<?> getAdminStats() {
                Map<String, Object> stats = new HashMap<>();
                stats.put("totalUsers", userRepository.count());
                stats.put("totalRestaurants", restaurantRepository.count());
                stats.put("pendingRestaurants", restaurantRepository.findAll().stream()
                                .filter(r -> r.getStatus() == Restaurant.ApprovalStatus.PENDING).count());
                stats.put("totalOrders", orderRepository.count());

        
                List<Order> allOrders = orderRepository.findAll();
                java.time.LocalDate today = java.time.LocalDate.now();

                long todayOrders = allOrders.stream()
                                .filter(o -> {
                                        if (o.getOrderTime() == null)
                                                return false;
                                        return o.getOrderTime().toInstant().atZone(java.time.ZoneId.systemDefault())
                                                        .toLocalDate()
                                                        .isEqual(today);
                                })
                                .count();

                double todayRevenue = allOrders.stream()
                                .filter(o -> {
                                        if (o.getOrderTime() == null)
                                                return false;
                                        return o.getOrderTime().toInstant().atZone(java.time.ZoneId.systemDefault())
                                                        .toLocalDate()
                                                        .isEqual(today);
                                })
                                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.getStatus())
                                                && !"REJECTED".equalsIgnoreCase(o.getStatus()))
                                .mapToDouble(o -> {
                                        Double price = o.getTotalPrice();
                                        if (price == null || price == 0.0) {
                                                price = o.getItems().stream()
                                                                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                                                                .sum();
                                        }
                                        return price;
                                })
                                .sum();

                stats.put("todayOrders", todayOrders);
                stats.put("todayRevenue", todayRevenue);

                return ResponseEntity.ok(stats);
        }

        @GetMapping("/restaurants")
        public ResponseEntity<?> getAllRestaurants() {
                List<Restaurant> restaurants = restaurantRepository.findAll();
                Map<String, Object> response = new HashMap<>();
                response.put("restaurants", restaurants);
                return ResponseEntity.ok(response);
        }


        @PutMapping("/restaurants/{id}/approve")
        public ResponseEntity<?> approveRestaurant(@PathVariable Long id) {
                Restaurant restaurant = restaurantRepository.findById(id).orElseThrow();
                restaurant.setStatus(Restaurant.ApprovalStatus.APPROVED);
                restaurantRepository.save(restaurant);
                return ResponseEntity.ok(Map.of("message", "Approved"));
        }

        
        @PutMapping("/restaurants/{id}/block")
        public ResponseEntity<?> blockRestaurant(@PathVariable Long id) {
                Restaurant restaurant = restaurantRepository.findById(id).orElseThrow();
                restaurant.setStatus(Restaurant.ApprovalStatus.REJECTED);
                restaurantRepository.save(restaurant);
                return ResponseEntity.ok(Map.of("message", "Blocked"));
        }

        
        @PutMapping("/restaurants/{id}/activate")
        public ResponseEntity<?> activateRestaurant(@PathVariable Long id) {
                Restaurant restaurant = restaurantRepository.findById(id).orElseThrow();
                restaurant.setStatus(Restaurant.ApprovalStatus.APPROVED);
                restaurantRepository.save(restaurant);
                return ResponseEntity.ok(Map.of("message", "Activated"));
        }


        @GetMapping("/users")
        public ResponseEntity<?> getAllUsers() {
                return ResponseEntity.ok(userRepository.findAll());
        }

        @PutMapping("/users/{id}/block")
        public ResponseEntity<?> blockUser(@PathVariable Long id) {
                User user = userRepository.findById(id).orElseThrow();
                user.setActive(false);
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "User blocked"));
        }

        
        @PutMapping("/users/{id}/activate")
        public ResponseEntity<?> activateUser(@PathVariable Long id) {
                User user = userRepository.findById(id).orElseThrow();
                user.setActive(true);
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "User activated"));
        }

        
        @GetMapping("/analytics")
        public ResponseEntity<?> getAnalytics() {
                List<Order> allOrders = orderRepository.findAll();

                long completedOrders = allOrders.stream()
                                .filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus())
                                                || "COMPLETED".equalsIgnoreCase(o.getStatus()))
                                .count();

                long cancelledOrders = allOrders.stream()
                                .filter(o -> "CANCELLED".equalsIgnoreCase(o.getStatus())
                                                || "REJECTED".equalsIgnoreCase(o.getStatus()))
                                .count();

                double totalRevenue = allOrders.stream()
                                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.getStatus())
                                                && !"REJECTED".equalsIgnoreCase(o.getStatus()))
                                .mapToDouble(o -> {
                                        Double price = o.getTotalPrice();
                                        if (price == null || price == 0.0) {
                                                price = o.getItems().stream()
                                                                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                                                                .sum();
                                        }
                                        return price;
                                })
                                .sum();

                
                Map<String, Long> restaurantOrderCounts = allOrders.stream()
                                .filter(o -> o.getRestaurant() != null)
                                .collect(Collectors.groupingBy(r -> r.getRestaurant().getName(),
                                                Collectors.counting()));

                List<Map<String, Object>> topRestaurants = restaurantOrderCounts.entrySet().stream()
                                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                                .limit(5)
                                .map(e -> {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("name", e.getKey());
                                        map.put("orders", e.getValue());
                                        return map;
                                })
                                .collect(Collectors.toList());

                Map<String, Object> analytics = new HashMap<>();
                analytics.put("totalOrders", allOrders.size());
                analytics.put("completedOrders", completedOrders);
                analytics.put("cancelledOrders", cancelledOrders);
                analytics.put("totalRevenue", totalRevenue);
                analytics.put("topRestaurants", topRestaurants);

                
                analytics.put("monthlyStats", List.of(
                                Map.of("month", "All Time", "orders", allOrders.size(), "revenue", totalRevenue)));

                return ResponseEntity.ok(analytics);
        }

        
        @GetMapping("/coupons")
        public ResponseEntity<?> getAllCoupons() {
                return ResponseEntity.ok(couponRepository.findAll());
        }

        @PostMapping("/coupons")
        public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
                if (couponRepository.findByCode(coupon.getCode()).isPresent()) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Coupon code already exists"));
                }
                return ResponseEntity.ok(couponRepository.save(coupon));
        }

        @DeleteMapping("/coupons/{id}")
        public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
                couponRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Coupon deleted"));
        }
}
