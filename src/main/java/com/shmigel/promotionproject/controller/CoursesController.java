package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.dto.*;
import com.shmigel.promotionproject.model.mapper.CourseMapper;
import com.shmigel.promotionproject.model.mapper.UserMapper;
import com.shmigel.promotionproject.service.CourseFeedbackService;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.impl.AuthenticationProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Log4j2
@RestController
@RequestMapping("/courses")
public class CoursesController {

    private CourseService courseService;

    private CourseMapper courseMapper;

    private UserMapper userMapper;

    private CourseFeedbackService courseFeedbackService;

    private LessonService lessonService;

    private AuthenticationProvider authenticationProvider;

    public CoursesController(CourseService courseService, CourseMapper courseMapper, UserMapper userMapper,
                             CourseFeedbackService courseFeedbackService, LessonService lessonService, AuthenticationProvider authenticationProvider) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
        this.userMapper = userMapper;
        this.courseFeedbackService = courseFeedbackService;
        this.lessonService = lessonService;
        this.authenticationProvider = authenticationProvider;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{courseId}/subscribe")
    public ResponseEntity<Void> subscribeToCourse(@PathVariable Long courseId) {
        log.info("Received request to subscribe to course with courseId: " + courseId);
        Long userId = authenticationProvider.getAuthentication().getUserId();
        courseService.addStudentToCourse(userId, courseId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CreateCourseDTO createCourseDTO) {
        log.info("Received request to create course with information: " + createCourseDTO);
        return ResponseEntity.ok(courseService.createCourse(createCourseDTO));
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'STUDENT')")
    @GetMapping
    public ResponseEntity<Collection<CourseDTO>> getUserCourses() {
        log.info("Received request to get user courses");
        return ResponseEntity.ok(courseMapper.toCourseDTOs(courseService.getUserCourses()));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{courseId}/students/{studentId}/status")
    public ResponseEntity<CourseStatusDTO> getUserCourseStatus(@PathVariable Long courseId, @PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.getCourseStatus(studentId, courseId));
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/{courseId}/students")
    public ResponseEntity<Collection<UserDTO>> getCourseStudents(@PathVariable Long courseId) {
        log.info("Received request to get students for course with id: " + courseId);
        return ResponseEntity.ok(courseService.getCourseStudents(courseId));
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @PostMapping("/{courseId}/students/{studentId}/feedback")
    public ResponseEntity<CourseFeedbackDTO> createFeedback(@PathVariable Long courseId, @PathVariable Long studentId,
                                                            @RequestBody String feedback) {
        return ResponseEntity.ok(courseFeedbackService.createFeedback(studentId, courseId, feedback));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{courseId}/lesson-details")
    public ResponseEntity<Collection<LessonDetailsDTO>> getCourseLessonDetails(@PathVariable Long courseId) {
        return ResponseEntity.ok(lessonService.getCourseLessonDetails(courseId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{courseId}/instructors/{instructorId}")
    public ResponseEntity<Void> setInstructorToCourse(@PathVariable Long courseId, @PathVariable Long instructorId) {
        courseService.assignInstructorToCourse(instructorId, courseId);
        return ResponseEntity.ok().build();
    }

}
