package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsDTO {

    private List<LessonDetailsDTO> lessonDetails;

    private CourseDTO course;

}
