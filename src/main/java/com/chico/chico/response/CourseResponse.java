package com.chico.chico.response;

import com.chico.chico.dto.UserDTO;
import com.chico.chico.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String image;
    private LocalDateTime createdAt;
    private String teacherName;
}
