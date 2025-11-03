package com.chico.chico.service;

import com.chico.chico.dto.ReviewDTO;
import com.chico.chico.entity.Review;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllCourseReviews(Long courseId);

    ReviewDTO addReview(String jwtToken, Review review, Long courseId);

    ReviewDTO editReview(String jwtToken, Review newReview, Long reviewId);

    void deleteReview(String jwtToken, Long reviewId);
}
