package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseFeedbackDTO {

    private Long id;

    private Long courseId;

    private Long studentId;

    private String feedback;

}
