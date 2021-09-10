package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.repository.LessonRepository;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import com.shmigel.promotionproject.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
public class LessonServiceImpl implements LessonService {

    private LessonRepository lessonRepository;

    private HomeworkService homeworkService;

    private AuthenticationProvider authenticationProvider;

    private UserService userService;

    private CourseService courseService;

    @Autowired
    public LessonServiceImpl(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    @Override
    public Lesson getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson with id: " + lessonId + " not found"));
    }

    @Override
    public Collection<Lesson> getCourseLessons(Long courseId) {
        return lessonRepository.findAllByCourseId(courseId);
    }

    @Override
    public Collection<Lesson> saveAll(Collection<Lesson> lessons) {
        return CollectionUtil.toCollection(lessonRepository.saveAll(lessons));
    }
}
