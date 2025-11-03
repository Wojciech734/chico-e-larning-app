package com.chico.chico.controller;

import com.chico.chico.dto.ReviewDTO;
import com.chico.chico.entity.Review;
import com.chico.chico.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;


    @GetMapping("/course/{courseId}")
    public List<ReviewDTO> getAllCourseReviews(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long courseId
    ) {
        return reviewService.getAllCourseReviews(courseId);
    }

    @PostMapping("/course/{courseId}")
    public ReviewDTO addReview(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody Review review,
            @PathVariable Long courseId
    ) {
        return reviewService.addReview(jwtToken, review, courseId);
    }

    @PutMapping("/review/{reviewId}")
    public ReviewDTO editReview(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody Review newReview,
            @PathVariable Long reviewId
    ) {
        return reviewService.editReview(jwtToken, newReview, reviewId);
    }

    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(jwtToken, reviewId);
        return ResponseEntity.ok("successfully deleted review");
    }
}
