package com.chico.chico.controller;

import com.chico.chico.dto.LessonDTO;
import com.chico.chico.entity.Lesson;
import com.chico.chico.request.ReorderLessonsRequest;
import com.chico.chico.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/lessons")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/course/{courseId}")
    public LessonDTO addLesson(
            @PathVariable Long courseId,
            @RequestBody Lesson lesson
    ) {
        return lessonService.addLesson(courseId, lesson);
    }

    @PutMapping("/lesson/{lessonId}")
    public LessonDTO editLesson(
            @PathVariable Long lessonId,
            @RequestBody Lesson updatedlesson
    ) {
        return lessonService.editLesson(lessonId, updatedlesson);
    }

    @DeleteMapping("/lesson/{lessonId}")
    public ResponseEntity<String> deleteLesson(
            @PathVariable Long lessonId
    ) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.ok("Lesson's been deleted successfully");
    }

    @PutMapping("/course/{courseId}/lessons/reorder")
    public ResponseEntity<String> reorderLessons(
            @PathVariable Long courseId,
            @RequestBody ReorderLessonsRequest request
            ) {
         lessonService.reorderLessons(courseId, request);
         return ResponseEntity.ok("Lessons have been reordered");
    }
}
