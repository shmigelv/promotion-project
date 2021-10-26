package com.shmigel.promotionproject.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.shmigel.promotionproject.exception.IllegalUserInputException;
import com.shmigel.promotionproject.model.Homework;
import com.shmigel.promotionproject.model.Instructor;
import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.repository.HomeworkRepository;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import com.shmigel.promotionproject.util.IOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class HomeworkServiceImpl implements HomeworkService {

    private final UserService userService;

    private final LessonService lessonService;

    private final HomeworkRepository homeworkRepository;

    private final AuthenticationProvider authenticationProvider;

    private final AmazonS3 s3Client;

    @Value("${aws.homeworks-path}")
    private String bucketName;

    @Autowired
    public HomeworkServiceImpl(UserService userService, LessonService lessonService,
                               HomeworkRepository homeworkRepository, AuthenticationProvider authenticationProvider,
                               AmazonS3 s3Client) {
        this.userService = userService;
        this.lessonService = lessonService;
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
        Student student = userService.getStudentById(studentId);
        Lesson lesson = lessonService.getLessonById(lessonId);

        if (!student.getCourses().contains(lesson.getCourse())) {
            throw new IllegalUserInputException("Student should be subscribed to lesson course");
        }

        Optional<Homework> homework = homeworkRepository.findByStudentIdAndLessonId(studentId, lessonId);
        if (homework.isPresent() && StringUtils.hasText(homework.get().getHomeworkFileKey())) {
            throw new IllegalUserInputException("Homework with file already has been submitted");
        } else if (homework.isPresent()) {
            homework.get().setHomeworkFileKey(saveHomeworkFile(lesson, student, file));
            return homeworkRepository.save(homework.get());
        } else {
            String fileKey = saveHomeworkFile(lesson, student, file);
            Homework newHomework = Homework.builder().lesson(lesson).student(student).homeworkFileKey(fileKey).build();
            return homeworkRepository.save(newHomework);
        }
    }

    protected String saveHomeworkFile(Lesson lesson, Student student, MultipartFile file) {
        final String key = student.getId() + "|" + lesson.getId();
        s3Client.putObject(bucketName, key, IOUtil.getInputStream(file), new ObjectMetadata());
        return key;
    }

    @Override
    public void putStudentMarkForLesson(Long studentId, Long lessonId, MarkDTO mark) {
        Lesson lesson = lessonService.getLessonById(lessonId);
        AuthenticationDTO authentication = authenticationProvider.getAuthentication();
        Instructor currentUser = userService.getInstructorById(authentication.getUserId());
        Student student = userService.getStudentById(studentId);

        if (!currentUser.getCourses().contains(lesson.getCourse())) {
            throw new IllegalUserInputException("Instructor can only put marks for his courses");
        }

        if (!student.getCourses().contains(lesson.getCourse())) {
            throw new IllegalUserInputException("Given student is not subscribed to that course");
        }

        Optional<Homework> homework = homeworkRepository.findByStudentIdAndLessonId(studentId, lessonId);
        if (homework.isPresent() && Objects.nonNull(homework.get().getMark())) {
            throw new IllegalUserInputException("Mark already has been set for this user lesson");
        } else if (homework.isPresent()) {
            homework.get().setMark(mark.getMark());
            homeworkRepository.save(homework.get());
        } else {
            homeworkRepository.save(Homework.builder().mark(mark.getMark()).lesson(lesson).student(student).build());
        }
    }

    @Override
    public Collection<Homework> getAllHomeworksByCourseIdAndStudentId(Long courseId, Long studentId) {
        return homeworkRepository.findAllByLessonCourseIdAndStudentId(courseId, studentId);
    }

}
