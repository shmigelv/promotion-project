package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.CourseFeedback;

public interface CourseFeedbackService {

    CourseFeedback createFeedback(Long studentId, Long courseId, String feedback);

}
