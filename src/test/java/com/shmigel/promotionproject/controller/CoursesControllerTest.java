package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.dto.CourseDTO;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.model.mapper.CourseMapper;
import com.shmigel.promotionproject.service.CourseFeedbackService;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.LessonService;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CoursesControllerTest {

    @Test
    void subscribeToCourse_checkResult() {
        //GIVEN
        var courseService = mock(CourseService.class);
        var sut = mock(CoursesController.class, withConstructor(courseService, null, null));
        doCallRealMethod().when(sut).subscribeToCourse(anyLong(), anyLong());

        //WHEN
        ResponseEntity<Void> actualResult = sut.subscribeToCourse(1L, 2L);

        //THEN
        verify(courseService).addStudentToCourse(eq(1L), eq(2L));
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
    }

    @Test
    void createCourse_checkResult() {
        //GIVEN
        var courseService = mock(CourseService.class);
        var sut = mock(CoursesController.class, withConstructor(courseService, null, null));
        doCallRealMethod().when(sut).createCourse(any());

        var createCourseDTO = mock(CreateCourseDTO.class);

        //WHEN
        ResponseEntity<CourseDTO> actualResult = sut.createCourse(createCourseDTO);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(courseService).createCourse(eq(createCourseDTO));
    }

    @Test
    void getUserCourses_checkResult() {
        //GIVEN
        var courseService = mock(CourseService.class);
        var sut = mock(CoursesController.class, withConstructor(courseService,null, null));
        doCallRealMethod().when(sut).getUserCourses();

        //WHEN
        ResponseEntity<?> actualResult = sut.getUserCourses();

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(courseService).getMappedUserCourses();
    }

    @Test
    void getUserCourseStatus_checkResult() {
        //GIVEN
        var courseService = mock(CourseService.class);
        var sut = mock(CoursesController.class, withConstructor(courseService, null, null));
        doCallRealMethod().when(sut).getStudentCourseStatus(anyLong());

        //WHEN
        ResponseEntity<?> actualResult = sut.getStudentCourseStatus(1L);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(courseService).getCourseStatus(eq(1L));
    }

    @Test
    void getCourseStudents_checkResult() {
        //GIVEN
        var courseService = mock(CourseService.class);
        var sut = mock(CoursesController.class, withConstructor(courseService, null, null));
        doCallRealMethod().when(sut).getCourseStudents(anyLong());

        //WHEN
        ResponseEntity<?> actualResult = sut.getCourseStudents(1L);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(courseService).getCourseStudents(eq(1L));
    }

    @Test
    void createFeedback_checkResult() {
        //GIVEN
        var courseFeedbackService = mock(CourseFeedbackService.class);
        var sut = mock(CoursesController.class, withConstructor(null, courseFeedbackService, null));
        doCallRealMethod().when(sut).createFeedback(anyLong(), anyLong(), anyString());

        //WHEN
        ResponseEntity<?> actualResult = sut.createFeedback(1L, 2L, "feedback");

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(courseFeedbackService).createFeedback(eq(2L), eq(1L), eq("feedback"));
    }

    @Test
    void getCourseLessonDetails_checkResult() {
        //GIVEN
        var lessonService = mock(LessonService.class);
        var sut = mock(CoursesController.class, withConstructor(null, null, lessonService));
        doCallRealMethod().when(sut).getCourseLessonDetails(anyLong());

        //WHEN
        ResponseEntity<?> actualResult = sut.getCourseLessonDetails(1L);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(lessonService).getCourseLessonDetails(eq(1L));
    }

    @Test
    void setInstructorToCourse_checkResult() {
        //GIVEN
        var courseService = mock(CourseService.class);
        var sut = mock(CoursesController.class, withConstructor(courseService, null, null));
        doCallRealMethod().when(sut).setInstructorToCourse(anyLong(), anyLong());

        //WHEN
        ResponseEntity<?> actualResult = sut.setInstructorToCourse(1L, 2L);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(courseService).assignInstructorToCourse(eq(2L), eq(1L));
    }

    private MockSettings withConstructor(CourseService courseService, CourseFeedbackService courseFeedbackService, LessonService lessonService) {
        return withSettings().useConstructor(courseService, courseFeedbackService, lessonService);
    }

}
