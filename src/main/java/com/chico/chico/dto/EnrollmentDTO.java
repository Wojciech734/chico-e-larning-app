package com.chico.chico.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long id;
    private String studentName;
    private String courseTitle;
    private int lessonsCompleted;
    private boolean finished;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedDate;
    private double progressPercent;
}
