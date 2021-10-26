package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.IllegalUserInputException;
import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.CourseFeedback;
import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import com.shmigel.promotionproject.model.mapper.CourseFeedbackMapper;
import com.shmigel.promotionproject.repository.CourseFeedbackRepository;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CourseFeedbackServiceImplTest {

    @Test
    void createFeedback_verifyException_whenFeedbackAlreadyExists() {
        //GIVEN
        var courseFeedbackRepository = mock(CourseFeedbackRepository.class);
        var sut = mock(CourseFeedbackServiceImpl.class, withConstructor(courseFeedbackRepository, null, null, null));
        doCallRealMethod().when(sut).createFeedback(anyLong(), anyLong(), anyString());

        when(courseFeedbackRepository.existsByStudentIdAndCourseId(anyLong(), anyLong())).thenReturn(true);

        //THEN
        var actualException = assertThrows(IllegalUserInputException.class, () -> sut.createFeedback(1L, 1L, "feedback"));
        assertEquals("Feedback already present for this students course", actualException.getMessage());
    }

    @Test
    void createFeedback_verifyCreateFeedback() {
        //GIVEN
        var courseFeedbackRepository = mock(CourseFeedbackRepository.class);
        var userService = mock(UserService.class);
        var courseService = mock(CourseService.class);
        var sut = mock(CourseFeedbackServiceImpl.class, withConstructor(courseFeedbackRepository, userService, courseService, mock(CourseFeedbackMapper.class)));
        doCallRealMethod().when(sut).createFeedback(anyLong(), anyLong(), anyString());

        when(courseFeedbackRepository.existsByStudentIdAndCourseId(anyLong(), anyLong())).thenReturn(false);

        var student = mock(Student.class);
        when(userService.getStudentById(anyLong())).thenReturn(student);

        var course = mock(Course.class);
        when(courseService.getCourseById(anyLong())).thenReturn(course);

        //WHEN
        sut.createFeedback(1L, 1L, "feedback");

        //THEN
        verify(courseFeedbackRepository).save(
                argThat(arg -> arg.getStudent().equals(student) &&
                        arg.getCourse().equals(course) &&
                        arg.getFeedback().equals("feedback")
                ));
    }

    @Test
    void createFeedback_verifyToCourseFeedbackDto() {
        //GIVEN
        var courseFeedbackRepository = mock(CourseFeedbackRepository.class);
        var userService = mock(UserService.class);
        var courseService = mock(CourseService.class);
        var courseFeedbackMapper = mock(CourseFeedbackMapper.class);

        var sut = mock(CourseFeedbackServiceImpl.class, withConstructor(courseFeedbackRepository, userService, courseService, courseFeedbackMapper));
        doCallRealMethod().when(sut).createFeedback(anyLong(), anyLong(), anyString());

        when(courseFeedbackRepository.existsByStudentIdAndCourseId(anyLong(), anyLong())).thenReturn(false);

        var courseFeedback = mock(CourseFeedback.class);
        when(courseFeedbackRepository.save(any())).thenReturn(courseFeedback);

        //WHEN
        sut.createFeedback(1L, 1L, "feedback");

        //THEN
        verify(courseFeedbackMapper).toCourseFeedbackDto(eq(courseFeedback));
    }

    @Test
    void validateStudentAndInstructorSubscribedToCourse_verifyException_whenStudentNotSubscribedToCourse() {
        //GIVEN
        var courseService = mock(CourseService.class);
        var authenticationProvider = mock(AuthenticationProvider.class);
        var sut = mock(CourseFeedbackServiceImpl.class, withConstructor(null, null, courseService, null, authenticationProvider));
        doCallRealMethod().when(sut).validateStudentAndInstructorSubscribedToCourse(any(), any());

        var authentication = mock(AuthenticationDTO.class);
        when(authenticationProvider.getAuthentication()).thenReturn(authentication);
        when(authentication.getRole()).thenReturn(Roles.ROLE_INSTRUCTOR);

        var student = mock(Student.class);
        when(student.getCourses()).thenReturn(List.of());

        var course = mock(Course.class);
        when(courseService.getUserCourses()).thenReturn(List.of(course));

        //THEN
        var actualException = assertThrows(IllegalUserInputException.class, () -> sut.validateStudentAndInstructorSubscribedToCourse(student, course));
        assertEquals("Student is not subscribed to course", actualException.getMessage());
    }

    @Test
    void validateStudentAndInstructorSubscribedToCourse_verifyException_whenInstructorNotSubscribedToCourse() {
        //GIVEN
        var courseService = mock(CourseService.class);
        var authenticationProvider = mock(AuthenticationProvider.class);
        var sut = mock(CourseFeedbackServiceImpl.class, withConstructor(null, null, courseService, null, authenticationProvider));
        doCallRealMethod().when(sut).validateStudentAndInstructorSubscribedToCourse(any(), any());

        var authentication = mock(AuthenticationDTO.class);
        when(authenticationProvider.getAuthentication()).thenReturn(authentication);
        when(authentication.getRole()).thenReturn(Roles.ROLE_INSTRUCTOR);

        var student = mock(Student.class);
        var course = mock(Course.class);
        when(student.getCourses()).thenReturn(List.of(course));

        when(courseService.getUserCourses()).thenReturn(List.of());

        //THEN
        var actualException = assertThrows(IllegalUserInputException.class, () -> sut.validateStudentAndInstructorSubscribedToCourse(student, course));
        assertEquals("Current user should be assigned as instructor to requested course", actualException.getMessage());
    }

    private MockSettings withConstructor(CourseFeedbackRepository courseFeedbackRepository, UserService userService,
                                         CourseService courseService, CourseFeedbackMapper courseFeedbackMapper) {
        return withConstructor(courseFeedbackRepository, userService, courseService, courseFeedbackMapper, mock(AuthenticationProvider.class));
    }

    private MockSettings withConstructor(CourseFeedbackRepository courseFeedbackRepository, UserService userService,
                                         CourseService courseService, CourseFeedbackMapper courseFeedbackMapper, AuthenticationProvider authenticationProvider) {
        return withSettings().useConstructor(courseFeedbackRepository, userService, courseService, courseFeedbackMapper, authenticationProvider);
    }

}