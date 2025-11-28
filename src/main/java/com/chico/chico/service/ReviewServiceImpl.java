package com.chico.chico.service;

import com.chico.chico.dto.ReviewDTO;
import com.chico.chico.entity.Course;
import com.chico.chico.entity.Review;
import com.chico.chico.entity.User;
import com.chico.chico.exception.CourseNotFoundException;
import com.chico.chico.exception.ReviewIsNullException;
import com.chico.chico.exception.ReviewNotFoundException;
import com.chico.chico.exception.UserNotFoundException;
import com.chico.chico.repository.CourseRepository;
import com.chico.chico.repository.ReviewRepository;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final JwtProvider jwtProvider;

    @Override
    public List<ReviewDTO> getAllCourseReviews(Long courseId) {
        return reviewRepository.findByCourseId(courseId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ReviewDTO addReview(Review review, Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (review == null) {
            throw new ReviewIsNullException("Review cannot be null");
        }

        if (course.getTeacher().equals(user)) {
            throw new RuntimeException("You cannot add reviews to your own course");
        }

        review.setCreatedAt(LocalDateTime.now());

        review.setAuthor(user);
        review.setCourse(course);

        return mapToDto(reviewRepository.save(review));
    }

    @Override
    public ReviewDTO editReview(Review newReview, Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));


        if (newReview.getRate() != 0.0) {
            review.setRate(newReview.getRate());
        }

        if (newReview.getComment() != null) {
            review.setComment(newReview.getComment());
        }

        review.setCreatedAt(LocalDateTime.now());

        return mapToDto(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (!review.getAuthor().equals(user)) {
            throw new RuntimeException("You're not the author of this review");
        }

        reviewRepository.delete(review);
    }

    public ReviewDTO mapToDto(Review review) {
        return new ReviewDTO(
                review.getId(),
                review.getAuthor().getFirstName() + " " + review.getAuthor().getLastName(),
                review.getRate(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
