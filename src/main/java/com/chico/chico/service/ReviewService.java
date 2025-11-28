package com.chico.chico.service;

import com.chico.chico.dto.ReviewDTO;
import com.chico.chico.entity.Review;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllCourseReviews(Long courseId);

    ReviewDTO addReview(Review review, Long courseId);

    ReviewDTO editReview(Review newReview, Long reviewId);

    void deleteReview(Long reviewId);
}
