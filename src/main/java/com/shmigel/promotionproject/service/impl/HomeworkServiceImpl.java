package com.shmigel.promotionproject.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.shmigel.promotionproject.exception.IlligalUserInputException;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.repository.HomeworkRepository;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import com.shmigel.promotionproject.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class HomeworkServiceImpl implements HomeworkService {

    private UserService userService;

    private LessonService lessonService;

    private CourseService courseService;

    private HomeworkRepository homeworkRepository;

    private AuthenticationProvider authenticationProvider;

    private final AmazonS3 s3Client;

    @Value("${aws.homeworks-path}")
    private String bucketName;

    @Autowired
    public HomeworkServiceImpl(UserService userService, LessonService lessonService,
                               HomeworkRepository homeworkRepository, CourseService courseService,
                               AuthenticationProvider authenticationProvider, AmazonS3 s3Client) {
        this.userService = userService;
        this.lessonService = lessonService;
        this.courseService = courseService;
        this.homeworkRepository = homeworkRepository;
        this.authenticationProvider = authenticationProvider;
        this.s3Client = s3Client;
    }

    @Override
    public Collection<Homework> getByStudentIdAndLessonIds(Long studentId, Collection<Long> lessonIds) {
        return homeworkRepository.findByStudentIdAndLessonIdIn(studentId, lessonIds);
    }

    @Override
    public Homework uploadHomework(Long studentId, Long lessonId, MultipartFile file) {
        User student = userService.findByIdAndRole(studentId, Roles.ROLE_STUDENT);
        Lesson lesson = lessonService.getLessonById(lessonId);

        boolean userSubscribedToCourse = courseService.isUserSubscribedToCourse(lesson.getCourse().getId(), studentId);
        if (!userSubscribedToCourse) {
            throw new IlligalUserInputException("Student should be subscribed to lessons' course");
        }

        Optional<Homework> homework = homeworkRepository.findByStudentIdAndLessonId(studentId, lessonId);
        if (homework.isPresent() && StringUtils.hasText(homework.get().getHomeworkFileKey())) {
            throw new IlligalUserInputException("Homework with file already has been submitted");
        } else if (homework.isPresent()) {
            homework.get().setHomeworkFileKey(saveHomeworkFile(lesson, student, file));
            return homeworkRepository.save(homework.get());
        } else {
            String fileKey = saveHomeworkFile(lesson, student, file);
            Homework newHomework = Homework.builder().lesson(lesson).student(student).homeworkFileKey(fileKey).build();
            return homeworkRepository.save(newHomework);
        }
    }

    private String saveHomeworkFile(Lesson lesson, User student, MultipartFile file) {
        final String key = student.getId() + "|" + lesson.getId() + "|" + UUID.randomUUID();
        s3Client.putObject(bucketName, key, IOUtil.getInputStream(file), new ObjectMetadata());
        return key;
    }

    @Override
    @Transactional
    public void putStudentMarkForLesson(Long studentId, Long lessonId, MarkDTO mark) {
        Lesson lesson = lessonService.getLessonById(lessonId);
        AuthenticationDTO authentication = authenticationProvider.getAuthentication();
        User currentUser = userService.findByIdAndRole(authentication.getUserId(), Roles.ROLE_INSTRUCTOR);
        User student = userService.findByIdAndRole(studentId, Roles.ROLE_STUDENT);

        if (!lesson.getCourse().getInstructors().contains(currentUser)) {
            throw new IlligalUserInputException("Instructor can only put marks for his courses");
        }

        if (!courseService.isUserSubscribedToCourse(lesson.getCourse().getId(), studentId)) {
            throw new IlligalUserInputException("Given student is not subscribed to that course");
        }

        Optional<Homework> homework = homeworkRepository.findByStudentIdAndLessonId(studentId, lessonId);
        if (homework.isPresent() && Objects.nonNull(homework.get().getMark())) {
            throw new IlligalUserInputException("Mark already has been set for this user lesson");
        } else {
            homeworkRepository.save(Homework.builder().mark(mark.getMark()).lesson(lesson).student(student).build());
        }
    }

    @Override
    public Collection<Homework> getAllHomeworksByCourseIdAndStudentId(Long courseId, Long studentId) {
        return homeworkRepository.findAllByLessonCourseIdAndStudentId(courseId, studentId);
    }

}
