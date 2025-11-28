package com.chico.chico.service;

import com.chico.chico.dto.EnrollmentDTO;

import java.util.List;

public interface EnrollmentService {

    EnrollmentDTO enrollCourse(Long courseId);
    List<EnrollmentDTO> getUserEnrollments();
    void markLessonAsCompleted(Long lessonId);
    void unEnrollFromCourse(Long courseId);
}
