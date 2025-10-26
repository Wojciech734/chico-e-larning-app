package com.chico.chico.service;

import com.chico.chico.entity.Course;
import com.chico.chico.entity.Lesson;
import com.chico.chico.response.CourseResponse;

import java.util.List;

public interface CourseService {

    CourseResponse createCourse(String jwtToken, Course course);

    void deleteCourse(String jwtToken, Long id);

    CourseResponse getCourseById(Long id);

    List<Course> getCoursesByCategory(String categoryName);
}
