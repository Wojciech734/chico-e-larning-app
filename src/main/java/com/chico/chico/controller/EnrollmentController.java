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
            @PathVariable Long courseId
    ) {
        return enrollmentService.enrollCourse(courseId);
    }

    @GetMapping
    public List<EnrollmentDTO> getUserEnrollments() {
        return enrollmentService.getUserEnrollments();
    }

    @PostMapping("/complete/lesson/{lessonId}")
    public ResponseEntity<String> markLessonAsCompleted(
            @PathVariable Long lessonId
    ) {
        enrollmentService.markLessonAsCompleted(lessonId);
        return ResponseEntity.ok("successfully marked as completed");
    }

    @DeleteMapping("/course/{courseId}")
    public ResponseEntity<String> unEnrollFromCourse(
            @PathVariable Long courseId
    ) {
        enrollmentService.unEnrollFromCourse(courseId);
        return ResponseEntity.ok("successfully un enrolled from course");
    }
}
