package com.chico.chico.service;

import com.chico.chico.entity.Course;
import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.exception.CourseNotFoundException;
import com.chico.chico.exception.NotTheOwnerException;
import com.chico.chico.exception.UserIsNotATeacherException;
import com.chico.chico.exception.UserNotFoundException;
import com.chico.chico.repository.CourseRepository;
import com.chico.chico.repository.LessonRepository;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.dto.CourseDTO;
import com.chico.chico.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Override
    public CourseDTO createCourse(Course course) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!teacher.getRoles().contains(Role.TEACHER)) {
            throw new UserIsNotATeacherException("No permissions to create courses, you're not a teacher");
        }

        course.setTeacher(teacher);

        return mapToDTO(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CourseNotFoundException("User not found"));

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getTeacher().equals(user)) {
            throw new NotTheOwnerException("You can only delete your own courses");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new UserIsNotATeacherException("Only teachers can delete courses");
        }

        courseRepository.delete(course);
    }

    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new CourseNotFoundException("Course not found"));
        return mapToDTO(course);
    }

    @Override
    public List<CourseDTO> getCoursesByCategory(String categoryName) {
        return courseRepository.findByCategoryName(categoryName)
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    public CourseDTO editCourse(Long courseId, Course updatedCourse) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CourseNotFoundException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found"));

        if (!course.getTeacher().equals(user)) {
            throw new NotTheOwnerException("You can only edit your own courses");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new UserIsNotATeacherException("Only teachers can edit courses");
        }

        if (updatedCourse.getCategory() != null) {
            course.setCategory(updatedCourse.getCategory());
        }

        if (updatedCourse.getImage() != null) {
            course.setImage(updatedCourse.getImage());
        }

        if (updatedCourse.getDescription() != null) {
            course.setDescription(updatedCourse.getDescription());
        }

        if (updatedCourse.getTitle() != null) {
            course.setTitle(updatedCourse.getTitle());
        }

        return mapToDTO(courseRepository.save(course));
    }

    private CourseDTO mapToDTO(Course course) {
        return new CourseDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getImage(),
                course.getCreatedAt(),
                course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName(),
                course.getCategory() != null ? course.getCategory().getName() : null,
                course.getLessons().size(),
                course.getAverageRating(),
                course.getStudentsCompleted()
        );
    }
}