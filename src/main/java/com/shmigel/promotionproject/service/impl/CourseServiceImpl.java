package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.exception.IlligalUserInputException;
import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.*;
import com.shmigel.promotionproject.model.mapper.CourseMapper;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.mapper.UserMapper;
import com.shmigel.promotionproject.repository.CourseRepository;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;

    private UserService userService;

    private LessonService lessonService;

    private HomeworkService homeworkService;

    private CourseMapper courseMapper;

    private UserMapper userMapper;

    private AuthenticationProvider authenticationProvider;

    @Autowired
    public void setHomeworkService(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    public CourseServiceImpl(CourseRepository courseRepository, UserService userService, LessonService lessonService,
                             CourseMapper courseMapper, UserMapper userMapper, AuthenticationProvider authenticationProvider) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.lessonService = lessonService;
        this.courseMapper = courseMapper;
        this.userMapper = userMapper;
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course with id: " + courseId + " not found"));
    }

    @Override
    @Transactional
    public void addStudentToCourse(Long studentId, Long courseId) {
        Course course = getCourseById(courseId);

        User student = userService.findByIdAndRole(studentId, Roles.ROLE_STUDENT);
        Collection<Course> userCourses = getStudentCourses(studentId);

        if (userCourses.contains(course)) {
            throw new IlligalUserInputException("User with id: " + studentId + "already subscribed to this course");
        }

        if (userCourses.size() >= 5) {
            throw new IlligalUserInputException("User with id: " + studentId + " can't subscribe to more than 5 courses");
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
    public Collection<UserDTO> getCourseStudents(Long courseId) {
        return userMapper.toUserDTOs(courseRepository.findById(courseId)
                .map(Course::getStudents).orElse(List.of()));
    }

    @Override
    @Transactional
    public CourseDTO createCourse(CreateCourseDTO createCourseDTO) {
        if (CollectionUtils.isEmpty(createCourseDTO.getInstructorIds())) {
            throw new IlligalUserInputException("Each course should have at least one instructor assigned to it");
        }
        if (createCourseDTO.getLessonsTiles().size() < 5) {
            throw new IlligalUserInputException("Course should have at least five lessons");
        }

        Collection<User> instructors = userService.findAllByIdsAndRole(createCourseDTO.getInstructorIds(), Roles.ROLE_INSTRUCTOR);

        Course course = courseRepository.save(new Course(createCourseDTO.getTitle(), instructors));
        lessonService.saveAll(createCourseDTO.getLessonsTiles().stream().map(Lesson::new).peek(course::addLesson).collect(Collectors.toList()));

        return courseMapper.toCourseDto(course);
    }

    @Override
    @Transactional
    public void assignInstructorToCourse(Long userId, Long courseId) {
        User instructor = userService.findByIdAndRole(userId, Roles.ROLE_INSTRUCTOR);

        Course course = getCourseById(courseId);
        course.getInstructors().add(instructor);
        courseRepository.save(course);
    }

    @Override
    public CourseStatusDTO getCourseStatus(Long studentId, Long courseId) {
        if (!isUserSubscribedToCourse(courseId, studentId)) {
            throw new IlligalUserInputException("Student should be subscribed to given course");
        }

        return new CourseStatusDTO(calculateCourseStatus(studentId, courseId));
    }

    private CourseStatus calculateCourseStatus(Long studentId, Long courseId) {
        Collection<Homework> userCourseHomeworks = homeworkService.getAllHomeworksByCourseIdAndStudentId(courseId, studentId);
        Long lessonsInCourse = lessonService.getNumberOfLessonsByCourse(courseId);

        if (userCourseHomeworks.size() != lessonsInCourse) {
            return CourseStatus.IN_PROGRESS;
        } else {
            int sumOfMarks = userCourseHomeworks.stream().mapToInt(Homework::getMark).sum();
            return sumOfMarks / lessonsInCourse >= 80 ? CourseStatus.PASSED : CourseStatus.FAILED;
        }
    }

    @Override
    public boolean isUserSubscribedToCourse(Long courseId, Long studentId) {
        return courseRepository.existsByIdAndStudentsId(courseId, studentId);
    }

}
