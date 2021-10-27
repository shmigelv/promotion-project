package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.dto.HomeworkDTO;
import com.shmigel.promotionproject.model.dto.MarkDTO;
import com.shmigel.promotionproject.model.mapper.HomeworkMapper;
import com.shmigel.promotionproject.service.HomeworkService;
import com.shmigel.promotionproject.service.impl.AuthenticationProvider;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LessonControllerTest {

    @Test
    void uploadHomework_checkResult() {
        //GIVEN
        var homeworkService = mock(HomeworkService.class);
        var authenticationProvider = mock(AuthenticationProvider.class);
        var sut = mock(LessonController.class, withConstructor(homeworkService, mock(HomeworkMapper.class), authenticationProvider));
        doCallRealMethod().when(sut).uploadHomework(anyLong(), any());

        when(authenticationProvider.getAuthenticatedUserId()).thenReturn(2L);

        var multipartFile = mock(MultipartFile.class);

        //WHEN
        ResponseEntity<?> actualResult = sut.uploadHomework(1L, multipartFile);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(homeworkService).uploadHomework(eq(2L), eq(1L), eq(multipartFile));
    }

    @Test
    void putMarkForStudentLesson_checkResult() {
        //GIVEN
        var homeworkService = mock(HomeworkService.class);
        var sut = mock(LessonController.class, withConstructor(homeworkService, mock(HomeworkMapper.class), mock(AuthenticationProvider.class)));
        doCallRealMethod().when(sut).putMarkForStudentLesson(anyLong(), anyLong(), any());

        var markDTO = mock(MarkDTO.class);

        //WHEN
        ResponseEntity<?> actualResult = sut.putMarkForStudentLesson(1L, 2L, markDTO);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(homeworkService).putStudentMarkForLesson(eq(2L), eq(1L), eq(markDTO));
    }

    private MockSettings withConstructor(HomeworkService homeworkService, HomeworkMapper homeworkMapper, AuthenticationProvider authenticationProvider) {
        return withSettings().useConstructor(homeworkService, homeworkMapper, authenticationProvider);
    }

}
