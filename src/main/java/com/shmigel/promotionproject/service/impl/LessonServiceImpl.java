package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.dto.LessonDetailsDTO;
import com.shmigel.promotionproject.repository.LessonRepository;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Autowired
    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public Lesson getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson with id: " + lessonId + " not found"));
    }

    @Override
    public Collection<Lesson> getCourseLessons(Long courseId) {
        return lessonRepository.findAllByCourseId(courseId);
    }

    @Override
    public Collection<Lesson> saveAll(Collection<Lesson> lessons) {
        return CollectionUtil.toCollection(lessonRepository.saveAll(lessons));
    }

    @Override
    public Long getNumberOfLessonsByCourse(Long courseId) {
        return lessonRepository.countByCourseId(courseId);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public Collection<LessonDetailsDTO> getCourseLessonDetails(Long courseId) {
        return lessonRepository.getCourseLessonDetails(courseId);
    }
}
