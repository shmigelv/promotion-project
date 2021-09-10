package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.repository.CourseRepository;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;

    private UserService userService;

    private AuthenticationProvider authenticationProvider;

    private LessonService lessonService;

    public CourseServiceImpl(CourseRepository courseRepository, UserService userService,
                             AuthenticationProvider authenticationProvider, LessonService lessonService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.authenticationProvider = authenticationProvider;
        this.lessonService = lessonService;
    }

    @Override
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course with id: " + courseId + " not found"));
    }

    @Override
    @Transactional
    public void addStudentToCourse(Long studentId, Long courseId) {
        Course course = getCourseById(courseId);

        User student = userService.findByIdAndRole(studentId, Roles.ROLE_STUDENT);
        Collection<Course> userCourses = getStudentCourses(studentId);

        if (userCourses.contains(course)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User with id: " + studentId + "already subscribed to this course");
        }

        if (userCourses.size() >= 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User with id: " + studentId + " can't subscribe to more than 5 courses");
        }

        course.getStudents().add(student);
        courseRepository.save(course);
    }

    @Override
    @Transactional
    public Collection<Course> getUserCourses() {
        AuthenticationDTO authentication = authenticationProvider.getAuthentication();
        User user = userService.getUserById(authentication.getUserId());

        if (user.getRole().equals(Roles.ROLE_INSTRUCTOR)) {
            return courseRepository.findAllByInstructorsId(user.getId());
        } else {
            return courseRepository.findAllByStudentsId(user.getId());
        }
    }

    @Override
    public Collection<Course> getStudentCourses(Long studentId) {
        return courseRepository.findAllByStudentsId(studentId);
    }

    @Override
    public Collection<User> getCourseStudents(Long courseId) {
        return courseRepository.findById(courseId)
                .map(Course::getStudents).orElse(List.of());
    }

    @Override
    @Transactional
    public Course createCourse(CreateCourseDTO createCourseDTO) {
        if (CollectionUtils.isEmpty(createCourseDTO.getInstructorIds())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Each course should have at least one instructor assigned to it");
        }
        Collection<User> instructors = userService.findAllByIdsAndRole(createCourseDTO.getInstructorIds(), Roles.ROLE_INSTRUCTOR);

        Course newCourse = Course.builder().title(createCourseDTO.getTitle()).instructors(instructors).build();
        Course course = courseRepository.save(newCourse);
        List<Lesson> lessons = createCourseDTO.getLessonsTiles().stream()
                .map(i -> new Lesson(i, course)).collect(Collectors.toList());
        lessonService.saveAll(lessons);
        return course;
    }

    @Override
    @Transactional
    public void assignInstructorToCourse(Long userId, Long courseId) {
        User instructor = userService.findByIdAndRole(userId, Roles.ROLE_INSTRUCTOR);

        Course course = getCourseById(courseId);
        course.getInstructors().add(instructor);
        courseRepository.save(course);
    }

}
