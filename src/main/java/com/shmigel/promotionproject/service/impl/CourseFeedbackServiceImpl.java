package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.CourseFeedback;
import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.repository.CourseFeedbackRepository;
import com.shmigel.promotionproject.service.CourseFeedbackService;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class CourseFeedbackServiceImpl implements CourseFeedbackService {

    private CourseFeedbackRepository courseFeedbackRepository;

    private UserService userService;

    private CourseService courseService;

    @Override
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public CourseFeedback createFeedback(Long studentId, Long courseId, String feedback) {
        Optional<CourseFeedback> courseFeedback = courseFeedbackRepository.findByStudentIdAndCourseId(studentId, courseId);

        if (courseFeedback.isPresent() && Objects.nonNull(courseFeedback.get().getFeedback())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback already present for this students course");
        }

        User student = userService.findByIdAndRole(studentId, Roles.ROLE_STUDENT);
        Course course = courseService.getCourseById(courseId);
        Collection<Course> instructorCourses = courseService.getUserCourses();
        Collection<Course> studentCourses = courseService.getStudentCourses(studentId);

        if (!instructorCourses.contains(course) || !studentCourses.contains(course)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error");
        }

        return courseFeedbackRepository.save(new CourseFeedback(course, student, feedback));
    }
}
