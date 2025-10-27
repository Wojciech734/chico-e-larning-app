package com.chico.chico.controller;

import com.chico.chico.dto.LessonDTO;
import com.chico.chico.entity.Course;
import com.chico.chico.dto.CourseDTO;
import com.chico.chico.service.CourseService;
import com.chico.chico.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final LessonService lessonService;

    @PostMapping("/create-course")
    public CourseDTO createCourse(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody Course course
    ) {
        return courseService.createCourse(jwtToken, course);
    }

    @GetMapping("/course/{id}")
    public CourseDTO getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @DeleteMapping("/course/{id}")
    public ResponseEntity<String> deleteCourse(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long id
    ) {
        courseService.deleteCourse(jwtToken, id);
        return ResponseEntity.ok("Successfully deleted course");
    }

    @GetMapping
    public List<CourseDTO> getCoursesByCategoryName(@RequestParam String name) {
        return courseService.getCoursesByCategory(name);
    }

    @PutMapping("/course/{id}")
    public CourseDTO editCourse(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long id,
            @RequestBody Course updatedCourse
    ) {
        return courseService.editCourse(jwtToken, id, updatedCourse);
    }

    @GetMapping("/course/{courseId}/lessons")
    public List<LessonDTO> getAllLessons(@PathVariable Long courseId) {
        return lessonService.getAllLessons(courseId);
    }
}