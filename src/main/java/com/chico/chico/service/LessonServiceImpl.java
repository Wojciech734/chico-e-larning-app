package com.chico.chico.service;

import com.chico.chico.dto.LessonDTO;
import com.chico.chico.entity.Course;
import com.chico.chico.entity.Lesson;
import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.repository.CourseRepository;
import com.chico.chico.repository.LessonRepository;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final JwtProvider jwtProvider;

    @Override
    public LessonDTO addLesson(String jwtToken, Long courseId, Lesson lesson) {
        String email = jwtProvider.extractEmailFromToken(jwtToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacher().equals(user)) {
            throw new RuntimeException("Only teachers can add, delete and edit lessons");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("Only teachers can add lessons");
        }

        lesson.setCourse(course);
        return mapToDTO(lessonRepository.save(lesson));
    }

    @Override
    public List<LessonDTO> getAllLessons(Long courseId) {
        List<Lesson> lessons = lessonRepository.findByCourseIdOrderByNumberAsc(courseId);
        return lessons.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public LessonDTO editLesson(String jwtToken, Long lessonId, Lesson updatedLesson) {

        String email = jwtProvider.extractEmailFromToken(jwtToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (!lesson.getCourse().getTeacher().equals(user)) {
            throw new RuntimeException("You can only edit your onw lessons and courses");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("Only teachers can add, delete and edit lessons");
        }

        if (updatedLesson.getTitle() != null) {
            lesson.setTitle(updatedLesson.getTitle());
        }

        if (updatedLesson.getNumber() != null) {
            lesson.setNumber(updatedLesson.getNumber());
        }

        if (updatedLesson.getPdfUrl() != null) {
            lesson.setPdfUrl(updatedLesson.getPdfUrl());
        }

        if (updatedLesson.getVideoUrl() != null) {
            lesson.setVideoUrl(updatedLesson.getVideoUrl());
        }

        if (updatedLesson.getContent() != null) {
            lesson.setContent(updatedLesson.getContent());
        }

        return mapToDTO(lessonRepository.save(lesson));
    }

    @Override
    public void deleteLesson(String jwtToken, Long lessonId) {
        String email = jwtProvider.extractEmailFromToken(jwtToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        if (!lesson.getCourse().getTeacher().equals(user)) {
            throw new RuntimeException("You can only delete your own lessons");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("Only teachers can add, delete and edit lessons");
        }

        lessonRepository.delete(lesson);
    }

    private LessonDTO mapToDTO(Lesson lesson) {
        return new LessonDTO(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getVideoUrl(),
                lesson.getNumber(),
                lesson.getContent(),
                lesson.getPdfUrl(),
                lesson.getCourse().getTitle()
        );
    }
}
