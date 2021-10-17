package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    @Test
    void assignRoleForUser_checkResult() {
        //GIVEN
        var userService = mock(UserService.class);
        var sut = mock(UserController.class, withConstructor(userService, null));
        doCallRealMethod().when(sut).assignRoleForUser(anyLong(), anyString());

        //WHEN
        ResponseEntity<?> actualResult = sut.assignRoleForUser(1L, "ROLE_STUDENT");

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(userService).setUserRole(eq(1L), eq("ROLE_STUDENT"));
    }

    @Test
    void currentUser_checkResult() {
        //GIVEN
        var securityService = mock(SecurityService.class);
        var sut = mock(UserController.class, withConstructor(null, securityService));
        doCallRealMethod().when(sut).currentUser();

        //WHEN
        ResponseEntity<?> actualResult = sut.currentUser();

        //THEN
        assertEquals(HttpStatus.OK, actualResult.getStatusCode());
        verify(securityService).getAuthenticatedUser();
    }

    protected MockSettings withConstructor(UserService userService, SecurityService securityService) {
        return withSettings().useConstructor(userService, securityService);
    }

}
