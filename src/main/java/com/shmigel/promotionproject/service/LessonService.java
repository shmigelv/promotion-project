package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Lesson;

import java.util.Collection;

public interface LessonService {

    Lesson getLessonById(Long lessonId);

    Collection<Lesson> getCourseLessons(Long courseId);

    Collection<Lesson> saveAll(Collection<Lesson> lessons);

    Long getNumberOfLessonsByCourse(Long courseId);

}