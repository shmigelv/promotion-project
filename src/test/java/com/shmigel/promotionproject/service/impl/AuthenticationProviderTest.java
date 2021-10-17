package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.GenericRestException;
import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationProviderTest {

    @Test
    void getAuthentication_verifyException_whenAuthenticationIsNull() {
        //GIVEN
        var sut = mock(AuthenticationProvider.class);
        doCallRealMethod().when(sut).getAuthentication();

        //THEN
        assertThrows(GenericRestException.class, sut::getAuthentication);
    }

    @Test
    void getAuthentication_verifyException_whenAuthenticationIsNotAuthenticated() {
        //GIVEN
        var sut = mock(AuthenticationProvider.class);
        doCallRealMethod().when(sut).getAuthentication();

        var authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //THEN
        assertThrows(GenericRestException.class, sut::getAuthentication);
    }

    @Test
    void getAuthentication_checkResult() {
        //GIVEN
        var sut = mock(AuthenticationProvider.class);
        doCallRealMethod().when(sut).getAuthentication();

        var authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);

        when(sut.convert(any(), any())).thenReturn("1");
        when(sut.mapToRole(any())).thenReturn(Roles.ROLE_STUDENT);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //WHEN
        final AuthenticationDTO actualAuthentication = sut.getAuthentication();

        //THEN
        assertEquals(1, actualAuthentication.getUserId());
        assertEquals(Roles.ROLE_STUDENT, actualAuthentication.getRole());
    }

    @Test
    void mapToRole_checkResult_whenMoreThanOneAuthorityProvided() {
        //GIVEN
        var sut = mock(AuthenticationProvider.class);
        doCallRealMethod().when(sut).mapToRole(any());

        //THEN
        assertThrows(GenericRestException.class, () -> sut.mapToRole(List.of(new SimpleGrantedAuthority("ROLE_UDENT"), new SimpleGrantedAuthority("ROLE_UDENT1"))));
    }

    @Test
    void mapToRole_checkResult_whenInvalidRoleNameProvided() {
        //GIVEN
        var sut = mock(AuthenticationProvider.class);
        doCallRealMethod().when(sut).mapToRole(any());

        //WHEN
        final Roles role = sut.mapToRole(List.of(new SimpleGrantedAuthority("ROLE_UDENT")));

        //THEN
        assertNull(role);
    }

    @Test
    void mapToRole_checkResult_whenValidRoleNameProvided() {
        //GIVEN
        var sut = mock(AuthenticationProvider.class);
        doCallRealMethod().when(sut).mapToRole(any());

        //WHEN
        final Roles role = sut.mapToRole(List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));

        //THEN
        assertEquals(Roles.ROLE_STUDENT, role);
    }

    @Test
    void convert_checkResult() {
        //GIVEN
        var sut = mock(AuthenticationProvider.class);
        doCallRealMethod().when(sut).convert(any(), any());

        //WHEN
        final String converted = sut.convert("qwe", String.class);

        //THEN
        assertNotNull(converted);
        assertEquals("qwe", converted);
    }

    @Test
    void convert_checkResult1() {
        //GIVEN
        var sut = mock(AuthenticationProvider.class);
        doCallRealMethod().when(sut).convert(any(), any());

        //THEN
        assertThrows(GenericRestException.class, () -> sut.convert("qwe", Integer.class));
    }

}