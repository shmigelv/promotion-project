package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseDTO {

    private String title;

    private List<Long> instructorIds;

    private List<String> lessonsTiles;

}
