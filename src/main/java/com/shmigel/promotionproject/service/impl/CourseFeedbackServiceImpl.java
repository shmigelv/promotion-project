package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.IllegalUserInputException;
import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.CourseFeedback;
import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.model.dto.CourseFeedbackDTO;
import com.shmigel.promotionproject.model.mapper.CourseFeedbackMapper;
import com.shmigel.promotionproject.repository.CourseFeedbackRepository;
import com.shmigel.promotionproject.service.CourseFeedbackService;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CourseFeedbackServiceImpl implements CourseFeedbackService {

    private final CourseFeedbackRepository courseFeedbackRepository;

    private final UserService userService;

    private final CourseService courseService;

    private final CourseFeedbackMapper courseFeedbackMapper;

    public CourseFeedbackServiceImpl(CourseFeedbackRepository courseFeedbackRepository, UserService userService,
                                     CourseService courseService, CourseFeedbackMapper courseFeedbackMapper) {
        this.courseFeedbackRepository = courseFeedbackRepository;
        this.userService = userService;
        this.courseService = courseService;
        this.courseFeedbackMapper = courseFeedbackMapper;
    }

    @Override
    public CourseFeedbackDTO createFeedback(Long studentId, Long courseId, String feedback) {
        if (courseFeedbackRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalUserInputException("Feedback already present for this students course");
        }

        Student student = userService.getStudentById(studentId);
        Course course = courseService.getCourseById(courseId);
        validateStudentAndInstructorSubscribedToCourse(student, course);

        CourseFeedback courseFeedback = courseFeedbackRepository.save(new CourseFeedback(course, student, feedback));
        return courseFeedbackMapper.toCourseFeedbackDto(courseFeedback);
    }

    protected void validateStudentAndInstructorSubscribedToCourse(Student student, Course course) {
        Collection<Course> instructorCourses = courseService.getUserCourses();
        Collection<Course> studentCourses = student.getCourses();

        if (!instructorCourses.contains(course)) {
            throw new IllegalUserInputException("Current user should be assigned as instructor to requested course");
        }
        if (!studentCourses.contains(course)) {
            throw new IllegalUserInputException("Student is not subscribed to course");
        }
    }
}
