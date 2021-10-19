package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonDetailsDTO {

    private Long id;

    private String title;

    private boolean homeworkUploaded;

    private MarkDTO mark;

}
