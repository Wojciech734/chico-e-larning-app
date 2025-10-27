package com.chico.chico.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {

    private Long id;
    private String title;
    private String videoUrl;
    private int number;
    private String content;
    private String pdfUrl;
    private String courseTitle;
}
