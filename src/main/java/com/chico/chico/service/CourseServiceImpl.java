package com.chico.chico.service;

import com.chico.chico.entity.Course;
import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.repository.CourseRepository;
import com.chico.chico.repository.LessonRepository;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.dto.CourseDTO;
import com.chico.chico.security.JwtProvider;
import lombok.RequiredArgsConstructor;
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
    public CourseDTO createCourse(String jwtToken, Course course) {

        String email = jwtProvider.extractEmailFromToken(jwtToken);

        User teacher = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!teacher.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("No permissions to create courses, you're not a teacher");
        }

        course.setTeacher(teacher);

        return mapToDTO(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(String jwtToken, Long id) {

        String email = jwtProvider.extractEmailFromToken(jwtToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacher().equals(user)) {
            throw new RuntimeException("You can only delete your own courses");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("Only teachers can delete courses");
        }

        courseRepository.delete(course);
    }

    @Override
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        return mapToDTO(course);
    }

    @Override
    public List<CourseDTO> getCoursesByCategory(String categoryName) {
        return courseRepository.findByCategoryName(categoryName)
                .stream().map(this::mapToDTO).toList();
    }

    @Override
    public CourseDTO editCourse(String jwtToken, Long courseId, Course updatedCourse) {

        String email = jwtProvider.extractEmailFromToken(jwtToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacher().equals(user)) {
            throw new RuntimeException("You can only edit your own courses");
        }

        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("Only teachers can edit courses");
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