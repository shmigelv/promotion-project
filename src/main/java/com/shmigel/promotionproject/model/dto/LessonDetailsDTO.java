package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonDetailsDTO {

    private Long id;

    private String title;

    private boolean homeworkUploaded;

    private Integer mark;

}
