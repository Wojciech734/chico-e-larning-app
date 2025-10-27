package com.chico.chico.controller;

import com.chico.chico.dto.LessonDTO;
import com.chico.chico.entity.Lesson;
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
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long courseId,
            @RequestBody Lesson lesson
    ) {
        return lessonService.addLesson(jwtToken, courseId, lesson);
    }

    @PutMapping("/lesson/{lessonId}")
    public LessonDTO editLesson(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long lessonId,
            @RequestBody Lesson updatedlesson
    ) {
        return lessonService.editLesson(jwtToken, lessonId, updatedlesson);
    }

    @DeleteMapping("/lesson/{lessonId}")
    public ResponseEntity<String> deleteLesson(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long lessonId
    ) {
        lessonService.deleteLesson(jwtToken, lessonId);
        return ResponseEntity.ok("Lesson's been deleted successfully");
    }
}
