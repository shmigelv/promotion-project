package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.Homework;
import com.shmigel.promotionproject.model.dto.HomeworkDTO;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.model.mapper.HomeworkMapper;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.impl.AuthenticationProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/lessons")
public class LessonController {

    private final HomeworkService homeworkService;

    private final HomeworkMapper homeworkMapper;

    private final AuthenticationProvider authenticationProvider;

    public LessonController(HomeworkService homeworkService, HomeworkMapper homeworkMapper, AuthenticationProvider authenticationProvider) {
        this.homeworkService = homeworkService;
        this.homeworkMapper = homeworkMapper;
        this.authenticationProvider = authenticationProvider;
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping("/{lessonId}/homeworks")
    public ResponseEntity<HomeworkDTO> uploadHomework(@PathVariable Long lessonId, @RequestParam("file") final MultipartFile file) {
        Long studentId = authenticationProvider.getAuthenticatedUserId();
        Homework homework = homeworkService.uploadHomework(studentId, lessonId, file);
        return ResponseEntity.ok(homeworkMapper.toHomeworkDTO(homework));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/{lessonId}/students/{studentId}/homeworks")
    public ResponseEntity<HomeworkDTO> uploadHomeworkForUser(@PathVariable Long lessonId, @PathVariable Long studentId,
                                                             @RequestParam("file") final MultipartFile file) {
        Homework homework = homeworkService.uploadHomework(studentId, lessonId, file);
        return ResponseEntity.ok(homeworkMapper.toHomeworkDTO(homework));
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    @PostMapping("/{lessonId}/students/{studentId}/marks")
    public ResponseEntity<Void> putMarkForStudentLesson(@PathVariable Long lessonId, @PathVariable Long studentId,
                                                        @RequestBody MarkDTO mark) {
        homeworkService.putStudentMarkForLesson(studentId, lessonId, mark);
        return ResponseEntity.ok().build();
    }

}
