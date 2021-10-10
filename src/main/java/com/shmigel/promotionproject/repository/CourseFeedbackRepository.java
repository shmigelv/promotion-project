package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.CourseFeedback;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CourseFeedbackRepository extends CrudRepository<CourseFeedback, Long> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

}
