package com.chico.chico.controller;

import com.chico.chico.dto.LessonDTO;
import com.chico.chico.entity.Lesson;
import com.chico.chico.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    private LessonService lessonService;

    @PostMapping("/add-lesson/{courseId}")
    public LessonDTO addLesson(
            @RequestHeader("Authorization") String jwtToken,
            @PathVariable Long courseId,
            @RequestBody Lesson lesson
    ) {
        return lessonService.addLesson(jwtToken, courseId, lesson);
    }

    @GetMapping("/{courseId}")
    public List<LessonDTO> getAllLessons(@PathVariable Long courseId) {
        return lessonService.getAllLessons(courseId);
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
