package com.chico.chico.service;

import com.chico.chico.entity.Course;
import com.chico.chico.entity.Lesson;
import com.chico.chico.entity.Role;
import com.chico.chico.entity.User;
import com.chico.chico.repository.CourseRepository;
import com.chico.chico.repository.LessonRepository;
import com.chico.chico.repository.UserRepository;
import com.chico.chico.response.CourseResponse;
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
    public CourseResponse createCourse(String jwtToken, Course course) {
        String cleanToken = jwtToken.replace("Bearer ", "");

        String email = jwtProvider.extractEmailFromToken(cleanToken);

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

        String clearToken = jwtToken.replace("Bearer ", "");
        String email = jwtProvider.extractEmailFromToken(clearToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getTeacher().equals(user) || !user.getRoles().contains(Role.TEACHER)) {
            throw new RuntimeException("No permissions to delete this course");
        }
        courseRepository.delete(course);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        return mapToDTO(course);
    }

    @Override
    public List<Course> getCoursesByCategory(String categoryName) {
        return courseRepository.findByCategoryName(categoryName);
    }

    private CourseResponse mapToDTO(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getImage(),
                course.getCreatedAt(),
                course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName()
        );
    }
}