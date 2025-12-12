package com.chico.chico.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long id;
    private String title;
    private String description;
    private String image;
    private LocalDateTime createdAt;
    private boolean isPublic;
    private String teacherName;
    private String categoryName;
    private int lessonsCount;
    private double averageRating;
    private int studentsCompleted;
}
