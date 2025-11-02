package com.chico.chico.repository;

import com.chico.chico.entity.Course;
import com.chico.chico.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentId(Long id);

    Optional<Enrollment> findEnrollmentByStudentIdAndCourseId(Long studentId, Long CourseId);
}
