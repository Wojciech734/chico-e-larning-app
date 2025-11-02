package com.chico.chico.service;

import com.chico.chico.dto.EnrollmentDTO;

import java.util.List;

public interface EnrollmentService {

    EnrollmentDTO enrollCourse(String jwtToken, Long courseId);
    List<EnrollmentDTO> getUserEnrollments(String jwtToken);
    void markLessonAsCompleted(String jwtToken, Long lessonId);
    void unEnrollFromCourse(String jwtToken, Long courseId);
}
