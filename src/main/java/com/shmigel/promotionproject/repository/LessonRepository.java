package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.dto.LessonDetailsDTO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LessonRepository extends CrudRepository<Lesson, Long> {

    Collection<Lesson> findAllByCourseId(Long courseId);

    long countByCourseId(Long courseId);

    @Query(value = "select new com.shmigel.promotionproject.model.dto.LessonDetailsDTO(l.id, l.title, h.homeworkFileKey is null, h.mark) from Lesson l " +
            "join l.course c " +
            "join l.homeworks h " +
            "where c.id = :course_id")
    List<LessonDetailsDTO> getCourseLessonDetails(@Param("course_id") Long courseId);
}
