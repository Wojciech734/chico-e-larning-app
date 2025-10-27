package com.chico.chico.service;

import com.chico.chico.dto.LessonDTO;
import com.chico.chico.entity.Lesson;

import java.util.List;

public interface LessonService {

    LessonDTO addLesson(String jwtToken, Long courseId, Lesson lesson);

    List<LessonDTO> getAllLessons(Long courseId);

    LessonDTO editLesson(String jwtToken, Long lessonId, Lesson updatedLesson);

    void deleteLesson(String jwtToken, Long lessonId);
}
