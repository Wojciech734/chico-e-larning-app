package com.chico.chico.service;

import com.chico.chico.dto.EnrollmentDTO;
import com.chico.chico.entity.*;
import com.chico.chico.exception.*;
import com.chico.chico.repository.*;
import com.chico.chico.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final JwtProvider jwtProvider;
    private final NotificationRepository notificationRepository;

    @Override
    public EnrollmentDTO enrollCourse(Long courseId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (enrollmentRepository.findEnrollmentByStudentIdAndCourseId(user.getId(), courseId).isPresent()) {
            throw new AlreadyEnrolledInException("You've already enrolled in this course");
        }

        Enrollment enrollment = new Enrollment();

        enrollment.setStudent(user);
        enrollment.setCourse(course);
        enrollment.setEnrolledAt(LocalDateTime.now());

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle("You've enrolled: " + course.getTitle());
        notification.setContent("");
        notification.setCourseId(courseId);

        notificationRepository.save(notification);

        return mapToDto(enrollmentRepository.save(enrollment));
    }

    @Override
    public List<EnrollmentDTO> getUserEnrollments() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return enrollmentRepository.findByStudentId(user.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void markLessonAsCompleted(Long lessonId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson not found"));

        Course course = lesson.getCourse();

        Enrollment enrollment = enrollmentRepository.findEnrollmentByStudentIdAndCourseId(user.getId(), course.getId())
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment not found"));

        LessonProgress lessonProgresses = enrollment.getLessonProgresses()
                .stream()
                .filter(lessonProgress -> lessonProgress.getLesson().equals(lesson))
                .findFirst()
                .orElseGet(() -> {
                    LessonProgress newLessonProgress = new LessonProgress();
                    newLessonProgress.setEnrollment(enrollment);
                    newLessonProgress.setLesson(lesson);
                    enrollment.getLessonProgresses().add(newLessonProgress);
                    return newLessonProgress;
                });
        lessonProgresses.setCompleted(true);
        lessonProgresses.setCompletedAt(LocalDateTime.now());

        enrollment.calculateCurrentProgress();
        enrollmentRepository.save(enrollment);
    }

    @Override
    public void unEnrollFromCourse(Long courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        Enrollment enrollment = enrollmentRepository.findEnrollmentByStudentIdAndCourseId(user.getId(), courseId)
                .orElseThrow(() -> new EnrollmentNotFoundException("enrollment not found"));

        enrollmentRepository.delete(enrollment);
    }

    private EnrollmentDTO mapToDto(Enrollment enrollment) {
        return new EnrollmentDTO(
                enrollment.getId(),
                enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName(),
                enrollment.getCourse().getTitle(),
                enrollment.getLessonsCompleted(),
                enrollment.isFinished(),
                enrollment.getEnrolledAt(),
                enrollment.getCompletedDate(),
                enrollment.getProgressPercent()
        );
    }
}