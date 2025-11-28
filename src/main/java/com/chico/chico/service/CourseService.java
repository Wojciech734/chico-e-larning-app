package com.chico.chico.service;

import com.chico.chico.entity.Course;
import com.chico.chico.dto.CourseDTO;

import java.util.List;

public interface CourseService {

    CourseDTO createCourse(Course course);

    void deleteCourse(Long id);

    CourseDTO getCourseById(Long id);

    List<CourseDTO> getCoursesByCategory(String categoryName);

    CourseDTO editCourse(Long courseId, Course updatedCourse);
}
