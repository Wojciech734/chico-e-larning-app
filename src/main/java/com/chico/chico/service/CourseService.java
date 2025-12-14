package com.chico.chico.service;

import com.chico.chico.entity.Course;
import com.chico.chico.dto.CourseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {

    CourseDTO createCourse(Course course);

    void deleteCourse(Long id);

    CourseDTO getCourseById(Long id);

    List<CourseDTO> getCoursesByCategory(String categoryName);

    CourseDTO editCourse(Long courseId, Course updatedCourse);

    void publishCourse(Long courseId);

    void hideCourse(Long courseId);

    List<CourseDTO> getAllTeacherCourses();

    List<CourseDTO> getPublicCourses();

    Page<CourseDTO> searchForCourses(String query, Pageable pageable);
}
