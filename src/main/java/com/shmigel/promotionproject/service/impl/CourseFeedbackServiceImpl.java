package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.IlligalUserInputException;
import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.CourseFeedback;
import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.CourseFeedbackDTO;
import com.shmigel.promotionproject.model.mapper.CourseFeedbackMapper;
import com.shmigel.promotionproject.repository.CourseFeedbackRepository;
import com.shmigel.promotionproject.service.CourseFeedbackService;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Service
public class CourseFeedbackServiceImpl implements CourseFeedbackService {

    private CourseFeedbackRepository courseFeedbackRepository;

    private UserService userService;

    private CourseService courseService;

    private CourseFeedbackMapper courseFeedbackMapper;

    public CourseFeedbackServiceImpl(CourseFeedbackRepository courseFeedbackRepository, UserService userService,
                                     CourseService courseService, CourseFeedbackMapper courseFeedbackMapper) {
        this.courseFeedbackRepository = courseFeedbackRepository;
        this.userService = userService;
        this.courseService = courseService;
        this.courseFeedbackMapper = courseFeedbackMapper;
    }

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public CourseFeedbackDTO createFeedback(Long studentId, Long courseId, String feedback) {
        if (courseFeedbackRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IlligalUserInputException("Feedback already present for this students course");
        }

        User student = userService.findByIdAndRole(studentId, Roles.ROLE_STUDENT);
        Course course = courseService.getCourseById(courseId);
        Collection<Course> instructorCourses = courseService.getUserCourses();
        Collection<Course> studentCourses = courseService.getStudentCourses(studentId);

        if (!instructorCourses.contains(course)) {
            throw new IlligalUserInputException("Current user should be assigned as instructor to requested course");
        }
        if (!studentCourses.contains(course)) {
            throw new IlligalUserInputException("Student is not subscribed to course");
        }

        CourseFeedback courseFeedback = courseFeedbackRepository.save(new CourseFeedback(course, student, feedback));
        return courseFeedbackMapper.toCourseFeedbackDto(courseFeedback);
    }
}
