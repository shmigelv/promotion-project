package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.Homework;
import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.dto.HomeworkDTO;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.model.mapper.HomeworkMapper;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    private LessonService lessonService;

    private HomeworkService homeworkService;

    private HomeworkMapper homeworkMapper;

    public LessonController(LessonService lessonService, HomeworkService homeworkService, HomeworkMapper homeworkMapper) {
        this.lessonService = lessonService;
        this.homeworkService = homeworkService;
        this.homeworkMapper = homeworkMapper;
    }

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<Collection<Lesson>> getCourseLessons(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getCourseLessons(courseId));
    }

    @PostMapping("/{lessonId}/students/{studentId}/homework")
    public ResponseEntity<HomeworkDTO> uploadHomework(@PathVariable Long lessonId, @PathVariable Long studentId, @RequestParam("file") final MultipartFile file) {
        Homework homework = homeworkService.uploadHomework(studentId, lessonId, file);
        return ResponseEntity.ok(homeworkMapper.toHomeworkDTO(homework));
    }

    @PostMapping("/{lessonId}/students/{studentId}/mark")
    public ResponseEntity<Void> putMarkForStudentLesson(@PathVariable Long lessonId, @PathVariable Long studentId,
                                                        @RequestBody MarkDTO mark) {
        homeworkService.putStudentMarkForLesson(studentId, lessonId, mark);
        return ResponseEntity.ok().build();
    }

}
