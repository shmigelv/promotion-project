package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.CourseFeedback;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.model.mapper.CourseMapper;
import com.shmigel.promotionproject.model.mapper.UserMapper;
import com.shmigel.promotionproject.model.dto.CourseDTO;
import com.shmigel.promotionproject.model.dto.UserDTO;
import com.shmigel.promotionproject.service.CourseFeedbackService;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.impl.AuthenticationProvider;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Log4j2
@RestController
@RequestMapping("/courses")
public class CoursesController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private CourseFeedbackService courseFeedbackService;

    @PostMapping("/{courseId}/subscribe")
    public void subscribeToCourse(@PathVariable Long courseId) {
        log.info("Received request to subscribe to course with courseId: " + courseId);
        Long userId = authenticationProvider.getAuthentication().getUserId();
        courseService.addStudentToCourse(userId, courseId);
    }

    @PostMapping
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CreateCourseDTO createCourseDTO) {
        log.info("Received request to create course with information: " + createCourseDTO);
        return ResponseEntity.ok(courseMapper.toCourseDto(courseService.createCourse(createCourseDTO)));
    }

    @GetMapping
    public ResponseEntity<Collection<CourseDTO>> getUserCourses() {
        log.info("Received request to get user courses");
        return ResponseEntity.ok(courseMapper.toCourseDTOs(courseService.getUserCourses()));
    }

    @GetMapping("/{courseId}/students")
    public ResponseEntity<Collection<UserDTO>> getCourseStudents(@PathVariable Long courseId) {
        log.info("Received request to get students for course with id: " + courseId);
        return ResponseEntity.ok(userMapper.toUserDTOs(courseService.getCourseStudents(courseId)));
    }

    @PutMapping("/{courseId}/instructors/{instructorId}")
    public ResponseEntity<Void> setInstructorToCourse(@PathVariable Long courseId, @PathVariable Long instructorId) {
        log.info("Received request to set instructor course with courseId: " + courseId + " ,instructorId: " + instructorId);
        courseService.assignInstructorToCourse(instructorId, courseId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{courseId}/students/{studentId}/feedback")
    public ResponseEntity<CourseFeedback> createFeedback(@PathVariable Long courseId, @PathVariable Long studentId,
                                                         @RequestBody String feedback) {
        return ResponseEntity.ok(courseFeedbackService.createFeedback(studentId, courseId, feedback));
    }

}
