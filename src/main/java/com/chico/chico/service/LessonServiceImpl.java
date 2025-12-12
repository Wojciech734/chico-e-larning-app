package com.chico.chico.service;

import com.chico.chico.dto.LessonDTO;
import com.chico.chico.entity.Course;
import com.chico.chico.entity.Lesson;
import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.exception.*;
import com.chico.chico.repository.CourseRepository;
import com.chico.chico.repository.LessonRepository;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.request.ReorderLessonsRequest;
import com.chico.chico.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final JwtProvider jwtProvider;

    @Override
    public LessonDTO addLesson(Long courseId, Lesson lesson) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getTeacher().equals(user)) {
            throw new UserIsNotATeacherException("Only teachers can add, delete and edit lessons");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new UserIsNotATeacherException("Only teachers can add lessons");
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
    public LessonDTO editLesson(Long lessonId, Lesson updatedLesson) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found"));

        if (!lesson.getCourse().getTeacher().equals(user)) {
            throw new NotTheOwnerException("You can only edit your onw lessons and courses");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new UserIsNotATeacherException("Only teachers can add, delete and edit lessons");
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
    public void deleteLesson(Long lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found"));

        if (!lesson.getCourse().getTeacher().equals(user)) {
            throw new NotTheOwnerException("You can only delete your own lessons");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new UserIsNotATeacherException("Only teachers can add, delete and edit lessons");
        }

        lessonRepository.delete(lesson);
    }

    /*
    *  Reorders lessons based on the provided list of lessons IDs.
    * Only the course owner can reorder lessons.
    * */
    @Override
    public void reorderLessons(Long courseId, ReorderLessonsRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getTeacher().equals(user)) {
            throw new NotTheOwnerException("You're not the owner of this course");
        }

        List<Lesson> lessons = lessonRepository
                .findByCourseIdOrderByNumberAsc(courseId);

        Map<Long, Lesson> map = lessons.stream()
                .collect(Collectors.toMap(Lesson::getId, l -> l));

        int index = 1;
        for (Long id : request.getLessonIds()) {
            Lesson lesson = map.get(id);
            if (lesson != null) {
                lesson.setNumber(index++);
            }
        }

        lessonRepository.saveAll(lessons);
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
