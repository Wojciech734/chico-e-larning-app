package com.chico.chico.service;

import com.chico.chico.dto.LessonDTO;
import com.chico.chico.entity.Lesson;
import com.chico.chico.request.ReorderLessonsRequest;

import java.util.List;

public interface LessonService {

    LessonDTO addLesson(Long courseId, Lesson lesson);

    List<LessonDTO> getAllLessons(Long courseId);

    LessonDTO editLesson(Long lessonId, Lesson updatedLesson);

    void deleteLesson(Long lessonId);

    void reorderLessons(Long courseId, ReorderLessonsRequest request);
}
