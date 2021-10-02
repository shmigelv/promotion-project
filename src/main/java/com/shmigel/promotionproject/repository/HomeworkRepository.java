package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.Homework;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface HomeworkRepository extends CrudRepository<Homework, Long> {

    Optional<Homework> findByStudentIdAndLessonId(Long studentId, Long lessonId);

    Collection<Homework> findByStudentIdAndLessonIdIn(Long studentId, Collection<Long> lessonIds);

    Collection<Homework> findAllByLessonCourseIdAndStudentId(Long courseId, Long studentId);

    boolean existsByLessonIdAndStudentId(Long lessonId, Long studentId);

}
