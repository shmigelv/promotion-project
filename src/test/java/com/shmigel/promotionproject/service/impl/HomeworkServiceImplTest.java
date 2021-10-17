package com.shmigel.promotionproject.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.shmigel.promotionproject.exception.IllegalUserInputException;
import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.repository.HomeworkRepository;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HomeworkServiceImplTest {

    @Test
    void getByStudentIdAndLessonIds_checkResult() {
        //GIVEN
        var homeworkRepository = mock(HomeworkRepository.class);
        var sut = mock(HomeworkServiceImpl.class, withConstructor(null, null, homeworkRepository, null, null));
        doCallRealMethod().when(sut).getByStudentIdAndLessonIds(anyLong(), anyCollection());

        var homework = mock(Homework.class);
        when(homeworkRepository.findByStudentIdAndLessonIdIn(anyLong(), anyCollection())).thenReturn(List.of(homework));

        //WHEN
        Collection<Homework> actualResult = sut.getByStudentIdAndLessonIds(1L, List.of());

        //THEN
        assertEquals(List.of(homework), actualResult);
    }

    @Test
    void uploadHomework_verifyException_whenUserIsNotSubscribedToTheCourse() {
        //GIVEN
        var homeworkRepository = mock(HomeworkRepository.class);
        var userService = mock(UserService.class);
        var lessonService = mock(LessonService.class);
        var sut = mock(HomeworkServiceImpl.class, withConstructor(userService, lessonService, homeworkRepository, null, null));
        doCallRealMethod().when(sut).uploadHomework(anyLong(), anyLong(), any());

        var course = mock(Course.class);

        var student = mock(Student.class);
        when(userService.getStudentById(anyLong())).thenReturn(student);

        var lesson = mock(Lesson.class);
        when(lesson.getCourse()).thenReturn(course);
        when(lessonService.getLessonById(anyLong())).thenReturn(lesson);

        //THEN
        var actualException = assertThrows(IllegalUserInputException.class, () -> sut.uploadHomework(1L, 1L, mock(MultipartFile.class)));
        assertEquals("Student should be subscribed to lesson course", actualException.getMessage());
    }

    @Test
    void uploadHomework_verifyException_whenHomeworkAlreadySaved() {
        //GIVEN
        var homeworkRepository = mock(HomeworkRepository.class);
        var userService = mock(UserService.class);
        var lessonService = mock(LessonService.class);
        var sut = mock(HomeworkServiceImpl.class, withConstructor(userService, lessonService, homeworkRepository, null, null));
        doCallRealMethod().when(sut).uploadHomework(anyLong(), anyLong(), any());

        var course = mock(Course.class);

        var student = mock(Student.class);
        when(userService.getStudentById(anyLong())).thenReturn(student);
        when(student.getCourses()).thenReturn(List.of(course));

        var lesson = mock(Lesson.class);
        when(lesson.getCourse()).thenReturn(course);
        when(lessonService.getLessonById(anyLong())).thenReturn(lesson);

        var homework = mock(Homework.class);
        when(homework.getHomeworkFileKey()).thenReturn("/file/key");
        when(homeworkRepository.findByStudentIdAndLessonId(anyLong(), anyLong())).thenReturn(Optional.of(homework));

        //THEN
        var actualException = assertThrows(IllegalUserInputException.class, () -> sut.uploadHomework(1L, 1L, mock(MultipartFile.class)));
        assertEquals("Homework with file already has been submitted", actualException.getMessage());
    }

    @Test
    void uploadHomework_verifySave_whenHomeworkWithoutFilePathExists() {
        //GIVEN
        var homeworkRepository = mock(HomeworkRepository.class);
        var userService = mock(UserService.class);
        var lessonService = mock(LessonService.class);
        var sut = mock(HomeworkServiceImpl.class, withConstructor(userService, lessonService, homeworkRepository, null, null));
        doCallRealMethod().when(sut).uploadHomework(anyLong(), anyLong(), any());

        var course = mock(Course.class);

        var student = mock(Student.class);
        when(userService.getStudentById(anyLong())).thenReturn(student);
        when(student.getCourses()).thenReturn(List.of(course));

        var lesson = mock(Lesson.class);
        when(lesson.getCourse()).thenReturn(course);
        when(lessonService.getLessonById(anyLong())).thenReturn(lesson);

        var homework = mock(Homework.class);
        when(homeworkRepository.findByStudentIdAndLessonId(anyLong(), anyLong())).thenReturn(Optional.of(homework));

        var file = mock(MultipartFile.class);
        when(sut.saveHomeworkFile(any(), any(), any())).thenReturn("/file/path");

        //WHEN
        sut.uploadHomework(1L, 1L, file);

        //THEN
        verify(sut).saveHomeworkFile(eq(lesson), eq(student), eq(file));
        verify(homework).setHomeworkFileKey(eq("/file/path"));
        verify(homeworkRepository).save(eq(homework));
    }

    @Test
    void uploadHomework_verifySave_whenHomeworkDoesntExist() {
        //GIVEN
        var homeworkRepository = mock(HomeworkRepository.class);
        var userService = mock(UserService.class);
        var lessonService = mock(LessonService.class);
        var sut = mock(HomeworkServiceImpl.class, withConstructor(userService, lessonService, homeworkRepository, null, null));
        doCallRealMethod().when(sut).uploadHomework(anyLong(), anyLong(), any());

        var course = mock(Course.class);

        var student = mock(Student.class);
        when(userService.getStudentById(anyLong())).thenReturn(student);
        when(student.getCourses()).thenReturn(List.of(course));

        var lesson = mock(Lesson.class);
        when(lesson.getCourse()).thenReturn(course);
        when(lessonService.getLessonById(anyLong())).thenReturn(lesson);

        when(sut.saveHomeworkFile(any(), any(), any())).thenReturn("/file/path");

        //WHEN
        var file = mock(MultipartFile.class);
        sut.uploadHomework(1L, 1L, file);

        //THEN
        verify(sut).saveHomeworkFile(eq(lesson), eq(student), eq(file));
        verify(homeworkRepository).save(argThat(
                arg -> arg.getLesson().equals(lesson) &&
                        arg.getStudent().equals(student) &&
                        arg.getHomeworkFileKey().equals("/file/path")
        ));
    }

    @Test
    void saveHomeworkFile_checkResult() {
        //GIVEN
        var amazonS3 = mock(AmazonS3.class);
        var sut = mock(HomeworkServiceImpl.class, withConstructor(null, null, null, null, amazonS3));
        doCallRealMethod().when(sut).saveHomeworkFile(any(), any(), any());

        var lesson = mock(Lesson.class);
        when(lesson.getId()).thenReturn(1L);

        var student = mock(Student.class);
        when(student.getId()).thenReturn(1L);

        //WHEN
        var actualResult = sut.saveHomeworkFile(lesson, student, mock(MultipartFile.class));

        //THEN
        verify(amazonS3).putObject(any(), eq("1|1"), any(), any());
        assertEquals("1|1", actualResult);
    }

    @Test
    void putStudentMarkForLesson_checkResult() {
        //GIVEN
        var homeworkRepository = mock(HomeworkRepository.class);
        var userService = mock(UserService.class);
        var lessonService = mock(LessonService.class);
        var authenticationProvider = mock(AuthenticationProvider.class);
        var sut = mock(HomeworkServiceImpl.class, withConstructor(userService, lessonService, homeworkRepository, authenticationProvider, null));
        doCallRealMethod().when(sut).putStudentMarkForLesson(anyLong(), anyLong(), any());

        var course = mock(Course.class);

        when(authenticationProvider.getAuthentication()).thenReturn(mock(AuthenticationDTO.class));

        var student = mock(Student.class);
        when(userService.getStudentById(anyLong())).thenReturn(student);
        when(student.getCourses()).thenReturn(List.of(course));

        var instructor = mock(Instructor.class);
        when(userService.getInstructorById(anyLong())).thenReturn(instructor);
        when(instructor.getCourses()).thenReturn(List.of(course));

        var lesson = mock(Lesson.class);
        when(lesson.getCourse()).thenReturn(course);
        when(lessonService.getLessonById(anyLong())).thenReturn(lesson);

        when(sut.saveHomeworkFile(any(), any(), any())).thenReturn("/file/path");

        var mark = mock(MarkDTO.class);
        when(mark.getMark()).thenReturn(10);

        //WHEN
        sut.putStudentMarkForLesson(1L, 1L, mark);

        //THEN
        verify(homeworkRepository).save(argThat(arg ->
                arg.getStudent().equals(student) &&
                arg.getMark().equals(10) &&
                arg.getLesson().equals(lesson)));
    }

    @Test
    void getAllHomeworksByCourseIdAndStudentId_checkResult() {
        //GIVEN
        var homeworkRepository = mock(HomeworkRepository.class);
        var sut = mock(HomeworkServiceImpl.class, withConstructor(null, null, homeworkRepository, null, null));
        doCallRealMethod().when(sut).getAllHomeworksByCourseIdAndStudentId(anyLong(), anyLong());


        //WHEN
        sut.getAllHomeworksByCourseIdAndStudentId(1L, 2L);

        //THEN
        verify(homeworkRepository).findAllByLessonCourseIdAndStudentId(eq(1L), eq(2L));
    }

    private MockSettings withConstructor(UserService userService, LessonService lessonService,
                                         HomeworkRepository homeworkRepository, AuthenticationProvider authenticationProvider,
                                         AmazonS3 s3Client) {
        return withSettings().useConstructor(userService, lessonService, homeworkRepository, authenticationProvider, s3Client);
    }

}