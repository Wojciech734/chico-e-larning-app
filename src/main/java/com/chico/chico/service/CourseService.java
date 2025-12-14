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

    Page<CourseDTO> getCoursesByCategory(String categoryName, Pageable pageable);

    CourseDTO editCourse(Long courseId, Course updatedCourse);

    void publishCourse(Long courseId);

    void hideCourse(Long courseId);

    List<CourseDTO> getAllTeacherCourses();

    Page<CourseDTO> getPublicCourses(Pageable pageable);

    Page<CourseDTO> searchForCourses(String query, Pageable pageable);
}
