package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.exception.IllegalUserInputException;
import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.*;
import com.shmigel.promotionproject.model.mapper.CourseMapper;
import com.shmigel.promotionproject.model.mapper.UserMapper;
import com.shmigel.promotionproject.repository.CourseRepository;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final UserService userService;

    private final LessonService lessonService;

    private final HomeworkService homeworkService;

    private final CourseMapper courseMapper;

    private final UserMapper userMapper;

    private final AuthenticationProvider authenticationProvider;

    public CourseServiceImpl(CourseRepository courseRepository, UserService userService, LessonService lessonService,
                             HomeworkService homeworkService, CourseMapper courseMapper, UserMapper userMapper,
                             AuthenticationProvider authenticationProvider) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.lessonService = lessonService;
        this.homeworkService = homeworkService;
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
    public void addStudentToCourse(Long courseId) {
        Long studentId = authenticationProvider.getAuthenticatedUserId();

        Course targetCourse = getCourseById(courseId);
        Student student = userService.getStudentById(studentId);
        Collection<Course> studentCourses = student.getCourses();

        if (studentCourses.contains(targetCourse)) {
            throw new IllegalUserInputException("User with id: " + studentId + " already subscribed to this course");
        }

        if (studentCourses.size() >= 5) {
            throw new IllegalUserInputException("User with id: " + studentId + " can't subscribe to more than 5 courses");
        }

        targetCourse.addStudent(student);
        courseRepository.save(targetCourse);
    }

    @Override
    public Collection<Course> getUserCourses() {
        Long currentUserId = authenticationProvider.getAuthenticatedUserId();
        User user = userService.getUserById(currentUserId);

        if (user.getRole().equals(Roles.ROLE_INSTRUCTOR)) {
            return courseRepository.findAllByInstructorsId(user.getId());
        } else if (user.getRole().equals(Roles.ROLE_STUDENT)){
            return courseRepository.findAllByStudentsId(user.getId());
        } else {
            return List.of();
        }
    }

    @Override
    public Collection<Course> getStudentCourses(Long studentId) {
        return courseRepository.findAllByStudentsId(studentId);
    }

    @Override
    public Collection<UserDTO> getCourseStudents(Long courseId) {
        return userMapper.toUserDTOsFromStudent(courseRepository.findById(courseId)
                .map(Course::getStudents).orElse(List.of()));
    }

    @Override
    public CourseDTO createCourse(CreateCourseDTO createCourseDTO) {
        if (CollectionUtils.isEmpty(createCourseDTO.getInstructorIds())) {
            throw new IllegalUserInputException("Each course should have at least one instructor assigned to it");
        }
        if (createCourseDTO.getLessonsTiles().size() < 5) {
            throw new IllegalUserInputException("Course should have at least five lessons");
        }

        Course course = new Course(createCourseDTO.getTitle());
        userService.getAllInstructors(createCourseDTO.getInstructorIds()).forEach(course::addInstructor);
        createCourseDTO.getLessonsTiles().stream().map(Lesson::new).forEach(course::addLesson);

        return courseMapper.toCourseDto(courseRepository.save(course));
    }

    @Override
    public void assignInstructorToCourse(Long instructorId, Long courseId) {
        Instructor instructor = userService.getInstructorById(instructorId);

        Course course = getCourseById(courseId);
        course.addInstructor(instructor);
        courseRepository.save(course);
    }

    @Override
    public CourseStatusDTO getCourseStatus(Long studentId, Long courseId) {
        if (!isUserSubscribedToCourse(courseId, studentId)) {
            throw new IllegalUserInputException("Student should be subscribed to given course");
        }
        return new CourseStatusDTO(calculateCourseStatus(studentId, courseId));
    }

    protected CourseStatus calculateCourseStatus(Long studentId, Long courseId) {
        Collection<Homework> userCourseHomeworks = homeworkService.getAllHomeworksByCourseIdAndStudentId(courseId, studentId);
        Long lessonsInCourse = lessonService.getNumberOfLessonsByCourse(courseId);

        long numberOfMarks = userCourseHomeworks.stream().map(Homework::getMark).filter(Objects::nonNull).count();
        if (numberOfMarks != lessonsInCourse) {
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
