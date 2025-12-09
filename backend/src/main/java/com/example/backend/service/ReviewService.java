package com.example.backend.service;

import com.example.backend.dto.ReviewRequest;
import com.example.backend.model.Order;
import com.example.backend.model.Restaurant;
import com.example.backend.model.Review;
import com.example.backend.model.User;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.RestaurantRepository;
import com.example.backend.repository.ReviewRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository,
            RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Review addReview(Long userId, ReviewRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("You can only review your own orders");
        }

        if (!"DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException("You can only review delivered orders");
        }

        if (reviewRepository.existsByOrderId(order.getId())) {
            throw new RuntimeException("You have already reviewed this order");
        }

        Restaurant restaurant = order.getRestaurant();

        Review review = new Review();
        review.setUser(user);
        review.setRestaurant(restaurant);
        review.setOrder(order);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        reviewRepository.save(review);

        updateRestaurantRating(restaurant);

        return review;
    }

    private void updateRestaurantRating(Restaurant restaurant) {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurant.getId());
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);


        avg = Math.round(avg * 10.0) / 10.0;

        restaurant.setRating(avg);
        restaurantRepository.save(restaurant);
    }
}
