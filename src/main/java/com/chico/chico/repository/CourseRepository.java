package com.chico.chico.repository;

import com.chico.chico.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findByCategoryName(String categoryName, Pageable pageable);

    List<Course> findByTeacherId(Long id);

    Page<Course> findByIsPublicTrue(Pageable pageable);

    /*
    * Custom query for course search by (title, description, teacher's first and last name)
    * - filters only public courses
    *
    * Pageable allows pagination and sorting.
    * */
    @Query(
            value = "SELECT c FROM Course c " +
                    "WHERE c.isPublic = true AND (" +
                    "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(c.teacher.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(c.teacher.lastName) LIKE LOWER(CONCAT('%', :query, '%'))" +
                    ")",
            countQuery = "SELECT COUNT(c) FROM Course c " +
                    "WHERE c.isPublic = true AND (" +
                    "LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(c.teacher.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                    "LOWER(c.teacher.lastName) LIKE LOWER(CONCAT('%', :query, '%'))" +
                    ")"
    )
    Page<Course> searchForCourses(@Param("query") String query, Pageable pageable);
}
