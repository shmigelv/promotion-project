package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeworkDTO {

    private Long id;

    private Long lessonId;

    private Long studentId;

    private boolean homeworkFilePresent;

}
