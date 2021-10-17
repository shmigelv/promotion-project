package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class SecurityControllerTest {

    @Test
    void login_checkResult() {
        //GIVEN
        var securityService = mock(SecurityService.class);
        var sut = mock(SecurityController.class, withConstructor(securityService));
        doCallRealMethod().when(sut).login(any());

        var userCredentialDTO = mock(UserCredentialDTO.class);

        //WHEN
        ResponseEntity<?> actualResult = sut.login(userCredentialDTO);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(securityService).login(eq(userCredentialDTO));
    }

    @Test
    void register_checkResult() {
        //GIVEN
        var securityService = mock(SecurityService.class);
        var sut = mock(SecurityController.class, withConstructor(securityService));
        doCallRealMethod().when(sut).register(any());

        var userCredentialDTO = mock(UserCredentialDTO.class);

        //WHEN
        ResponseEntity<?> actualResult = sut.register(userCredentialDTO);

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(securityService).register(eq(userCredentialDTO));
    }

    private MockSettings withConstructor(SecurityService securityService) {
        return withSettings().useConstructor(securityService);
    }

}
