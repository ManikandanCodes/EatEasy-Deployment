package com.example.backend.controller;

import com.example.backend.dto.ReviewRequest;
import com.example.backend.model.Review;
import com.example.backend.model.User;
import com.example.backend.service.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin("*")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review addReview(@RequestBody ReviewRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return reviewService.addReview(user.getId(), request);
    }
}
