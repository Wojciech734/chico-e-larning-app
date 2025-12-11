package com.chico.chico.request;

import lombok.Data;

import java.util.List;

@Data
public class ReorderLessonsRequest {
    private List<Long> lessonIds;
}
