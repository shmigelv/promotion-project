package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.model.Lesson;
import com.shmigel.promotionproject.model.dto.LessonDetailsDTO;
import com.shmigel.promotionproject.repository.LessonRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonServiceImplTest {

    @Test
    void getLessonById_checkResult_whenLessonExists() {
        //GIVEN
        var lessonRepository = mock(LessonRepository.class);
        var sut = mock(LessonServiceImpl.class, withConstructor(lessonRepository));
        doCallRealMethod().when(sut).getLessonById(anyLong());

        var lesson = mock(Lesson.class);
        when(lessonRepository.findById(anyLong())).thenReturn(Optional.of(lesson));

        //WHEN
        final Lesson actualResult = sut.getLessonById(1L);

        //THEN
        assertSame(lesson, actualResult);
    }

    @Test
    void getLessonById_verifyException_whenLessonDoesntExist() {
        //GIVEN
        var lessonRepository = mock(LessonRepository.class);
        var sut = mock(LessonServiceImpl.class, withConstructor(lessonRepository));
        doCallRealMethod().when(sut).getLessonById(anyLong());

        when(lessonRepository.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThrows(EntityNotFoundException.class, () -> sut.getLessonById(1L));
    }

    @Test
    void getCourseLessons_checkResult() {
        //GIVEN
        var lessonRepository = mock(LessonRepository.class);
        var sut = mock(LessonServiceImpl.class, withConstructor(lessonRepository));
        doCallRealMethod().when(sut).getCourseLessons(anyLong());

        var lesson = mock(Lesson.class);
        when(lessonRepository.findAllByCourseId(anyLong())).thenReturn(List.of(lesson));

        //WHEN
        final Collection<Lesson> courseLessons = sut.getCourseLessons(1L);

        //THEN
        assertEquals(List.of(lesson), courseLessons);
    }

    @Test
    void saveAll_checkResult() {
        //GIVEN
        var lessonRepository = mock(LessonRepository.class);
        var sut = mock(LessonServiceImpl.class, withConstructor(lessonRepository));
        doCallRealMethod().when(sut).saveAll(anyCollection());

        var lesson = mock(Lesson.class);
        when(lessonRepository.saveAll(anyCollection())).thenReturn(List.of(lesson));

        //WHEN
        final Collection<Lesson> savedLessons = sut.saveAll(List.of(mock(Lesson.class)));

        //THEN
        assertEquals(List.of(lesson), savedLessons);
    }

    @Test
    void getNumberOfLessonsByCourse_checkResult() {
        //GIVEN
        var lessonRepository = mock(LessonRepository.class);
        var sut = mock(LessonServiceImpl.class, withConstructor(lessonRepository));
        doCallRealMethod().when(sut).getNumberOfLessonsByCourse(anyLong());

        var lesson = mock(Lesson.class);
        when(lessonRepository.countByCourseId(anyLong())).thenReturn(3L);

        //WHEN
        final Long numberOfLessonsByCourse = sut.getNumberOfLessonsByCourse(anyLong());

        //THEN
        assertEquals(3L, numberOfLessonsByCourse);
    }

    @Test
    void getCourseLessonDetails_checkResult() {
        //GIVEN
        var lessonRepository = mock(LessonRepository.class);
        var sut = mock(LessonServiceImpl.class, withConstructor(lessonRepository));
        doCallRealMethod().when(sut).getCourseLessonDetails(anyLong());

        var lesson = mock(LessonDetailsDTO.class);
        when(lessonRepository.getCourseLessonDetails(anyLong())).thenReturn(List.of(lesson));

        //WHEN
        final Collection<LessonDetailsDTO> courseLessonDetails = sut.getCourseLessonDetails(anyLong());

        //THEN
        assertEquals(List.of(lesson), courseLessonDetails);
    }

    private MockSettings withConstructor(LessonRepository lessonRepository) {
        return withSettings().useConstructor(lessonRepository);
    }

}