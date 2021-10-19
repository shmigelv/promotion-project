package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.Course;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

    Collection<Course> findAllByStudentsId(Long studentId);

    Collection<Course> findAllByInstructorsId(Long instructorsId);

    Optional<Course> findByLessonsId(Long lessonsId);

    boolean existsByIdAndStudentsId(Long courseId, Long studentId);

    @EntityGraph(attributePaths = {"instructors", "lessons", "lessons.homeworks"})
    Optional<Course> findDetailedById(Long courseId);
}
