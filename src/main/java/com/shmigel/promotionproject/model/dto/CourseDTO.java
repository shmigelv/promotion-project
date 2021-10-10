package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private Long id;

    private String title;

    private Collection<Long> instructorIds;

    private Collection<Long> studentIds;

    private Collection<Long> lessonIds;

}
