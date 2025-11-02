package com.chico.chico.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"student", "course"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(
        name = "enrollments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"})
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private int lessonsCompleted = 0;
    private boolean finished = false;
    private LocalDateTime enrolledAt = LocalDateTime.now();
    private LocalDateTime completedDate;
    private double progressPercent = 0.0;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LessonProgress> lessonProgresses = new HashSet<>();

    public void calculateCurrentProgress() {
        int lessonsNumber = (course != null && course.getLessons() != null) ? course.getLessons().size() : 0;
        long completed = lessonProgresses.stream().filter(LessonProgress::isCompleted).count();
        this.lessonsCompleted = (int) completed;
        this.progressPercent = (lessonsNumber != 0) ? (double) (completed * 100) / lessonsNumber : 0;
        if (lessonsNumber > 0 && lessonsCompleted >= lessonsNumber) {
            if (this.completedDate == null) {
                this.completedDate = LocalDateTime.now();
            }
            this.finished = true;
        } else {
            this.finished = false;
        }
    }
}
