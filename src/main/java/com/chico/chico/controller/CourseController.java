package com.chico.chico.controller;

import com.chico.chico.entity.Course;
import com.chico.chico.response.CourseResponse;
import com.chico.chico.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/create-course")
    public CourseResponse createCourse(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody Course course
    ) {
        return courseService.createCourse(jwtToken, course);
    }

    @GetMapping("/course/{id}")
    public CourseResponse getCourseById(@PathVariable Long id) {
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
    public List<Course> getCoursesByCategoryName(@RequestBody String name) {
        return courseService.getCoursesByCategory(name);
    }
}