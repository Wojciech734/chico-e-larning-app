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
            @PathVariable Long courseId
    ) {
        return reviewService.getAllCourseReviews(courseId);
    }

    @PostMapping("/course/{courseId}")
    public ReviewDTO addReview(
            @RequestBody Review review,
            @PathVariable Long courseId
    ) {
        return reviewService.addReview(review, courseId);
    }

    @PutMapping("/review/{reviewId}")
    public ReviewDTO editReview(
            @RequestBody Review newReview,
            @PathVariable Long reviewId
    ) {
        return reviewService.editReview(newReview, reviewId);
    }

    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("successfully deleted review");
    }
}
