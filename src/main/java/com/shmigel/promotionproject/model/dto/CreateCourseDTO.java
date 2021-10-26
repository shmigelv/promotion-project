package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseDTO {

    private String title;

    private Collection<Long> instructorIds;

    private Collection<String> lessonsTiles;

}
