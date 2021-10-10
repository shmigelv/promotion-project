package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.dto.LessonDetailsDTO;

import java.util.Collection;

public interface LessonService {

    Lesson getLessonById(Long lessonId);

    Collection<Lesson> getCourseLessons(Long courseId);

    Collection<Lesson> saveAll(Collection<Lesson> lessons);

    Long getNumberOfLessonsByCourse(Long courseId);

    Collection<LessonDetailsDTO> getCourseLessonDetails(Long courseId);

}