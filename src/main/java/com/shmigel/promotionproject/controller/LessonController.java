package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.dto.UserLessonOverviewDTO;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private HomeworkService homeworkService;

    @GetMapping("/courses/{courseId}/lessons")
    public ResponseEntity<Collection<Lesson>> getCourseLessons(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getCourseLessons(courseId));
    }

    @PostMapping("/{lessonId}/homework")
    public ResponseEntity<Void> uploadHomework(@PathVariable Long lessonId, @RequestParam("file") final MultipartFile file) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{lessonId}/students/{studentId}/mark")
    public ResponseEntity<Void> putMarkForStudentLesson(@PathVariable Long lessonId, @PathVariable Long studentId,
                                                        @RequestBody MarkDTO mark) {
        homeworkService.putStudentMarkForLesson(studentId, lessonId, mark);
        return ResponseEntity.ok().build();
    }

}
