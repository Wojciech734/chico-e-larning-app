package com.chico.chico.service;

import com.chico.chico.entity.Course;
import com.chico.chico.dto.CourseDTO;

import java.util.List;

public interface CourseService {

    CourseDTO createCourse(String jwtToken, Course course);

    void deleteCourse(String jwtToken, Long id);

    CourseDTO getCourseById(Long id);

    List<CourseDTO> getCoursesByCategory(String categoryName);

    CourseDTO editCourse(String jwtToken, Long courseId, Course updatedCourse);
}
