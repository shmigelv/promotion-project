package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import com.shmigel.promotionproject.repository.HomeworkRepository;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import com.shmigel.promotionproject.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class HomeworkServiceImpl implements HomeworkService {

    private UserService userService;

    private LessonService lessonService;

    private CourseService courseService;

    private HomeworkRepository homeworkRepository;

    private AuthenticationProvider authenticationProvider;

    @Autowired
    public HomeworkServiceImpl(UserService userService, LessonService lessonService,
                               HomeworkRepository homeworkRepository, CourseService courseService,
                               AuthenticationProvider authenticationProvider) {
        this.userService = userService;
        this.lessonService = lessonService;
        this.courseService = courseService;
        this.homeworkRepository = homeworkRepository;
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public Collection<Homework> getByStudentIdAndLessonIds(Long studentId, Collection<Long> lessonIds) {
        return homeworkRepository.findByStudentIdAndLessonIdIn(studentId, lessonIds);
    }

    @Override
    public void uploadHomework(Long studentId, Long lessonId, MultipartFile file) {
        User student = userService.findByIdAndRole(studentId, Roles.ROLE_STUDENT);
        Lesson lesson = lessonService.getLessonById(lessonId);

        Optional<Homework> homework = homeworkRepository.findByStudentIdAndLessonId(studentId, lessonId);

        if (homework.isPresent() && Objects.nonNull(homework.get().getHomeworkFile())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Homework with file already has been submitted");
        } else {
            createHomework(lesson, student, file);
        }
    }

    @Override
    public Homework createHomework(Lesson lesson, User student, MultipartFile file) {
        String fileText = IOUtil.read(file);
        HomeworkFile homeworkFile = new HomeworkFile(file.getOriginalFilename(), fileText, IOUtil.getMd5Checksum(fileText));
        Homework newHomework = Homework.builder().lesson(lesson).student(student).homeworkFile(homeworkFile).build();
        return homeworkRepository.save(newHomework);
    }

    @Override
    @Transactional
    public void putStudentMarkForLesson(Long studentId, Long lessonId, MarkDTO mark) {
        Lesson lesson = lessonService.getLessonById(lessonId);
        AuthenticationDTO authentication = authenticationProvider.getAuthentication();
        User currentUser = userService.findByIdAndRole(authentication.getUserId(), Roles.ROLE_INSTRUCTOR);
        User student = userService.findByIdAndRole(studentId, Roles.ROLE_STUDENT);

        if (!lesson.getCourse().getInstructors().contains(currentUser)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Instructor can only put marks for his courses");
        }

        Collection<Course> studentCourses = courseService.getStudentCourses(studentId);
        if (!studentCourses.contains(lesson.getCourse())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Given student is not subscribed to that course");
        }

        Optional<Homework> homework = homeworkRepository.findByStudentIdAndLessonId(studentId, lessonId);
        if (homework.isPresent() && Objects.nonNull(homework.get().getMark())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mark already has been set for this user lesson");
        } else {
            homeworkRepository.save(Homework.builder().mark(mark.getMark()).lesson(lesson).student(student).build());
        }
    }

}
