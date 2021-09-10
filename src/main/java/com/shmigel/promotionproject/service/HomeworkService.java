package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.model.Homework;
import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface HomeworkService {

    Collection<Homework> getByStudentIdAndLessonIds(Long studentId, Collection<Long> lessonIds);

    void uploadHomework(Long studentId, Long lessonId, MultipartFile file);

    Homework createHomework(Lesson lesson, User student, MultipartFile file);

    void putStudentMarkForLesson(Long studentId, Long lessonId, MarkDTO mark);
}
