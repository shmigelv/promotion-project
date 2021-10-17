package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.*;
import com.shmigel.promotionproject.model.dto.CourseStatusDTO;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.model.mapper.CourseMapper;
import com.shmigel.promotionproject.model.mapper.UserMapper;
import com.shmigel.promotionproject.repository.CourseRepository;
import com.shmigel.promotionproject.repository.UserRepository;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.LessonService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockSettings;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

public class CourseServiceImplTest {

    @Test
    void getCourseById_checkResult() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, null, null, null, null, null, null));
        doCallRealMethod().when(sut).getCourseById(anyLong());

        var course = mock(Course.class);
        when(courseRepository.findById(eq(1L))).thenReturn(Optional.of(course));

        //WHEN
        var actualResult = sut.getCourseById(1L);

        //THEN
        assertEquals(course, actualResult);
    }

    @Test
    void addStudentToCourse_verifySave() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var authenticationProvider = mock(AuthenticationProvider.class);
        var userService = mock(UserService.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, userService, null, null, null, null, authenticationProvider));
        doCallRealMethod().when(sut).addStudentToCourse(anyLong());

        var course = mock(Course.class);
        when(sut.getCourseById(anyLong())).thenReturn(course);

        when(authenticationProvider.getAuthenticatedUserId()).thenReturn(1L);

        var student = mock(Student.class);
        when(userService.getStudentById(anyLong())).thenReturn(student);

        var studentCourses = mock(Collection.class);
        when(student.getCourses()).thenReturn(studentCourses);

        var savedCourse = mock(Course.class);
        when(courseRepository.save(any())).thenReturn(savedCourse);

        //WHEN
        sut.addStudentToCourse(1L);

        //THEN
        verify(courseRepository).save(eq(course));
    }

    @Test
    void getUserCourses_checkResult_whenUserHasStudentRole() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var authenticationProvider = mock(AuthenticationProvider.class);
        var userService = mock(UserService.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, userService, null, null, null, null, authenticationProvider));
        doCallRealMethod().when(sut).getUserCourses();

        when(authenticationProvider.getAuthenticatedUserId()).thenReturn(1L);

        var user = mock(User.class);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(user.getRole()).thenReturn(Roles.ROLE_STUDENT);

        var studentCourses = mock(Collection.class);
        when(courseRepository.findAllByStudentsId(anyLong())).thenReturn(studentCourses);

        //WHEN
        var actualResult = sut.getUserCourses();

        //THEN
        assertEquals(studentCourses, actualResult);
    }

    @Test
    void getUserCourses_checkResult_whenUserHasInstructorRole() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var authenticationProvider = mock(AuthenticationProvider.class);
        var userService = mock(UserService.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, userService, null, null, null, null, authenticationProvider));
        doCallRealMethod().when(sut).getUserCourses();

        when(authenticationProvider.getAuthenticatedUserId()).thenReturn(1L);

        var user = mock(User.class);
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(user.getRole()).thenReturn(Roles.ROLE_INSTRUCTOR);

        var instructorCourses = mock(Collection.class);
        when(courseRepository.findAllByInstructorsId(anyLong())).thenReturn(instructorCourses);

        //WHEN
        var actualResult = sut.getUserCourses();

        //THEN
        assertEquals(instructorCourses, actualResult);
    }

    @Test
    void getStudentCourses_checkResult() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, null, null, null, null, null, null));
        doCallRealMethod().when(sut).getStudentCourses(anyLong());

        var collection = mock(Collection.class);
        when(courseRepository.findAllByStudentsId(eq(1L))).thenReturn(collection);

        //WHEN
        var actualResult = sut.getStudentCourses(1L);

        //THEN
        assertEquals(collection, actualResult);
    }

    @Test
    void getCourseStudents_verifyToUserDtoFromStudents() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var userMapper = mock(UserMapper.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, null, null, null, null, userMapper, null));
        doCallRealMethod().when(sut).getCourseStudents(anyLong());

        var course = mock(Course.class);
        when(courseRepository.findById(eq(1L))).thenReturn(Optional.of(course));

        var collection = mock(Collection.class);
        when(course.getStudents()).thenReturn(collection);

        //WHEN
        var actualResult = sut.getCourseStudents(1L);

        //THEN
        verify(userMapper).toUserDTOsFromStudent(eq(collection));
    }

    @Test
    void createCourse_verifyToUserDtoFromStudents() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var userMapper = mock(UserMapper.class);
        var userService = mock(UserService.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, userService, null, null, mock(CourseMapper.class), userMapper, null));
        doCallRealMethod().when(sut).createCourse(any());

        var createCourseDTO = mock(CreateCourseDTO.class);
        when(createCourseDTO.getTitle()).thenReturn("title");
        when(createCourseDTO.getInstructorIds()).thenReturn(List.of(1L));

        var lessonTitles = mock(Collection.class);
        when(lessonTitles.size()).thenReturn(5);
        when(createCourseDTO.getLessonsTiles()).thenReturn(lessonTitles);

        var course = mock(Course.class);
        when(courseRepository.findById(eq(1L))).thenReturn(Optional.of(course));

        var collection = mock(Collection.class);
        when(course.getStudents()).thenReturn(collection);

        var instructor = mock(Instructor.class);
        when(userService.getAllInstructors(any())).thenReturn(List.of(instructor));

        //WHEN
        sut.createCourse(createCourseDTO);

        //THEN
        courseRepository.save(argThat(
                arg -> arg.getInstructors().equals(List.of(instructor)) &&
                        arg.getTitle().equals("title")
        ));
    }

    @Test
    void assignInstructorToCourse_checkResult() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var userService = mock(UserService.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, userService, null, null, null, null, null));
        doCallRealMethod().when(sut).assignInstructorToCourse(anyLong(), anyLong());

        var instructor = mock(Instructor.class);
        when(userService.getInstructorById(anyLong())).thenReturn(instructor);

        var course = mock(Course.class);
        when(sut.getCourseById(anyLong())).thenReturn(course);

        //WHEN
        sut.assignInstructorToCourse(1L, 2L);

        //THEN
        verify(course).addInstructor(eq(instructor));
        verify(courseRepository).save(eq(course));
    }

    @Test
    void getCourseStatus_checkResult() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var userService = mock(UserService.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, userService, null, null, null, null, null));
        doCallRealMethod().when(sut).getCourseStatus(anyLong(), anyLong());

        when(sut.isUserSubscribedToCourse(anyLong(), anyLong())).thenReturn(true);
        when(sut.calculateCourseStatus(anyLong(), anyLong())).thenReturn(CourseStatus.PASSED);

        //WHEN
        CourseStatusDTO actualResult = sut.getCourseStatus(1L, 2L);

        //THEN
        assertEquals(CourseStatus.PASSED, actualResult.getStatus());
    }

    @ParameterizedTest
    @MethodSource("calculateCourseStatus_checkResult_arguments")
    void calculateCourseStatus_checkResult(Integer homeworkMark, CourseStatus expectedCourseStatus) {
        //GIVEN
        var homeworkService = mock(HomeworkServiceImpl.class);
        var lessonService = mock(LessonServiceImpl.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(null, null, lessonService, homeworkService, null, null, null));
        doCallRealMethod().when(sut).calculateCourseStatus(anyLong(), anyLong());

        var homework = mock(Homework.class);
        when(homework.getMark()).thenReturn(homeworkMark);
        when(homeworkService.getAllHomeworksByCourseIdAndStudentId(anyLong(), anyLong())).thenReturn(List.of(homework));

        when(lessonService.getNumberOfLessonsByCourse(anyLong())).thenReturn(1L);

        //WHEN
        CourseStatus actualResult = sut.calculateCourseStatus(1L, 2L);

        //THEN
        assertEquals(expectedCourseStatus, actualResult);
    }

    static Stream<Arguments> calculateCourseStatus_checkResult_arguments() {
        return Stream.of(
                Arguments.of(20, CourseStatus.FAILED),
                Arguments.of(79, CourseStatus.FAILED),
                Arguments.of(0, CourseStatus.FAILED),
                Arguments.of(80, CourseStatus.PASSED),
                Arguments.of(100, CourseStatus.PASSED)
        );
    }

    @Test
    void isUserSubscribedToCourse_checkResult() {
        //GIVEN
        var courseRepository = mock(CourseRepository.class);
        var sut = mock(CourseServiceImpl.class, withConstructor(courseRepository, null, null, null, null, null, null));
        doCallRealMethod().when(sut).isUserSubscribedToCourse(anyLong(), anyLong());

        when(courseRepository.existsByIdAndStudentsId(anyLong(), anyLong())).thenReturn(false);

        //WHEN
        var actualResult = sut.isUserSubscribedToCourse(1L, 2L);

        //THEN
        assertFalse(actualResult);
    }

    private MockSettings withConstructor(CourseRepository courseRepository, UserService userService, LessonService lessonService,
                                         HomeworkService homeworkService, CourseMapper courseMapper, UserMapper userMapper,
                                         AuthenticationProvider authenticationProvider) {
        return withSettings().useConstructor(courseRepository, userService, lessonService, homeworkService, courseMapper, userMapper, authenticationProvider);
    }

}
