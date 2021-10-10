package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.dto.CourseFeedbackDTO;

public interface CourseFeedbackService {

    CourseFeedbackDTO createFeedback(Long studentId, Long courseId, String feedback);

}
