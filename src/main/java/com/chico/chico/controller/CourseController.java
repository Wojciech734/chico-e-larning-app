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
            @RequestBody Course course
    ) {
        return courseService.createCourse(course);
    }

    @GetMapping("/course/{id}")
    public CourseDTO getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @DeleteMapping("/course/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Long id
    ) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Successfully deleted course");
    }

    @GetMapping
    public List<CourseDTO> getCoursesByCategoryName(@RequestParam String name) {
        return courseService.getCoursesByCategory(name);
    }

    @PutMapping("/course/{id}")
    public CourseDTO editCourse(
            @PathVariable Long id,
            @RequestBody Course updatedCourse
    ) {
        return courseService.editCourse(id, updatedCourse);
    }

    @GetMapping("/course/{courseId}/lessons")
    public List<LessonDTO> getAllLessons(@PathVariable Long courseId) {
        return lessonService.getAllLessons(courseId);
    }

    @PutMapping("/publish/{courseId}")
    public ResponseEntity<String> publishCourse(@PathVariable Long courseId) {
        courseService.publishCourse(courseId);
        return ResponseEntity.ok("Successfully published course");
    }

    @PutMapping("/hide/{courseId}")
    public ResponseEntity<String> hideCourse(@PathVariable Long courseId) {
        courseService.hideCourse(courseId);
        return ResponseEntity.ok("Successfully hided course");
    }

    @GetMapping("/my/courses")
    public List<CourseDTO> getAllTeacherCourses() {
        return courseService.getAllTeacherCourses();
    }
}