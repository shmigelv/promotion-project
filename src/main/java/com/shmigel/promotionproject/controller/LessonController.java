package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.Homework;
import com.shmigel.promotionproject.model.dto.HomeworkDTO;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.model.mapper.HomeworkMapper;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.impl.AuthenticationProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    private LessonService lessonService;

    private HomeworkService homeworkService;

    private HomeworkMapper homeworkMapper;

    private AuthenticationProvider authenticationProvider;

    public LessonController(LessonService lessonService, HomeworkService homeworkService, HomeworkMapper homeworkMapper,
                            AuthenticationProvider authenticationProvider) {
        this.lessonService = lessonService;
        this.homeworkService = homeworkService;
        this.homeworkMapper = homeworkMapper;
        this.authenticationProvider = authenticationProvider;
    }

    @PostMapping("/{lessonId}/homeworks")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<HomeworkDTO> uploadHomework(@PathVariable Long lessonId, @RequestParam("file") final MultipartFile file) {
        Long studentId = authenticationProvider.getAuthentication().getUserId();
        Homework homework = homeworkService.uploadHomework(studentId, lessonId, file);
        return ResponseEntity.ok(homeworkMapper.toHomeworkDTO(homework));
    }

    @PostMapping("/{lessonId}/students/{studentId}/marks")
    public ResponseEntity<Void> putMarkForStudentLesson(@PathVariable Long lessonId, @PathVariable Long studentId,
                                                        @RequestBody MarkDTO mark) {
        homeworkService.putStudentMarkForLesson(studentId, lessonId, mark);
        return ResponseEntity.ok().build();
    }

}
