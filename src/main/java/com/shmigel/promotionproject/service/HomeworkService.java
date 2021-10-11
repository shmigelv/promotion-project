package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Homework;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

public interface HomeworkService {

    Collection<Homework> getByStudentIdAndLessonIds(Long studentId, Collection<Long> lessonIds);

    Homework uploadHomework(Long studentId, Long lessonId, MultipartFile file);

    void putStudentMarkForLesson(Long studentId, Long lessonId, MarkDTO mark);

    Collection<Homework> getAllHomeworksByCourseIdAndStudentId(Long courseId, Long studentId);
}
