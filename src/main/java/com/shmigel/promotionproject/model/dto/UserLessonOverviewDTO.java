package com.shmigel.promotionproject.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLessonOverviewDTO {

    private Long id;

    private String title;

    private Integer mark;

}
