package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.Lesson;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, Long> {

    Collection<Lesson> findAllByCourseId(Long courseId);

    long countByCourseId(Long courseId);
}
