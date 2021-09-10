package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO {

    private Long id;

    private String title;

    private Long courseId;

}
