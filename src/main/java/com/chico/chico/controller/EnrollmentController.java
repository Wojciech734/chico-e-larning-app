package com.chico.chico.controller;

import com.chico.chico.dto.EnrollmentDTO;
import com.chico.chico.entity.Enrollment;
import com.chico.chico.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/course/{courseId}")
    public EnrollmentDTO enrollCourse(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long courseId
    ) {
        return enrollmentService.enrollCourse(jwtToken, courseId);
    }

    @GetMapping
    public List<EnrollmentDTO> getUserEnrollments(
            @RequestHeader("Authorization") String jwtToken
    ) {
        return enrollmentService.getUserEnrollments(jwtToken);
    }

    @PostMapping("/complete/lesson/{lessonId}")
    public ResponseEntity<String> markLessonAsCompleted(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long lessonId
    ) {
        enrollmentService.markLessonAsCompleted(jwtToken, lessonId);
        return ResponseEntity.ok("successfully marked as completed");
    }

    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<String> unEnrollFromCourse(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long courseId
    ) {
        enrollmentService.unEnrollFromCourse(jwtToken, courseId);
        return ResponseEntity.ok("successfully un enrolled from course");
    }
}
