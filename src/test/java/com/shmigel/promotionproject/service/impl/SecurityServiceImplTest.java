package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.config.properties.JwtProperties;
import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.exception.IllegalUserInputException;
import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import com.shmigel.promotionproject.model.dto.JwtDTO;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.mapper.UserMapper;
import com.shmigel.promotionproject.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.io.Encoders;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.MockSettings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SecurityServiceImplTest {

    @Test
    void login_verifyException_whenUserWithUsernameNotFound() {
        //GIVEN
        var userService = mock(UserServiceImpl.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, null, null, null, null));
        doCallRealMethod().when(sut).login(any());

        when(userService.getUserByUsername(anyString())).thenReturn(null);

        //THEN
        var actualException = assertThrows(EntityNotFoundException.class, () -> sut.login(mock(UserCredentialDTO.class)));
        assertEquals("Can't find user for given credentials", actualException.getMessage());
    }

    @Test
    void login_verifyException_whenUserWhenUserPasswordIsDifferent() {
        //GIVEN
        var userService = mock(UserServiceImpl.class);
        var passwordEncoder = mock(PasswordEncoder.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, passwordEncoder, null, null, null));
        doCallRealMethod().when(sut).login(any());

        when(userService.getUserByUsername(anyString())).thenReturn(mock(User.class));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        //THEN
        var actualException = assertThrows(EntityNotFoundException.class, () -> sut.login(mock(UserCredentialDTO.class)));
        assertEquals("Can't find user for given credentials", actualException.getMessage());
    }

    @Test
    void login_verifyGenerateJwt() {
        //GIVEN
        var userService = mock(UserServiceImpl.class);
        var passwordEncoder = mock(PasswordEncoder.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, passwordEncoder, null, null, null));
        doCallRealMethod().when(sut).login(any());

        when(userService.getUserByUsername(any())).thenReturn(mock(User.class));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        var jwt = mock(JwtDTO.class);
        when(sut.generateJwt(any())).thenReturn(jwt);

        //WHEN
        var actualResponse = sut.login(mock(UserCredentialDTO.class));

        //THEN
        assertSame(jwt, actualResponse);
    }

    @Test
    void generateJwt_checkResult() {
        //GIVEN
        var jwtProperties = mock(JwtProperties.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(null, jwtProperties, null, null, null, null));
        doCallRealMethod().when(sut).generateJwt(any());

        when(sut.getDefaultTokenExpirationDate()).thenReturn(new Date(1600000000000L));

        when(jwtProperties.getKey()).thenReturn(getTestSecretKey());
        when(jwtProperties.getIssuer()).thenReturn("issuer");

        var user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Roles.ROLE_STUDENT);

        //WHEN
        JwtDTO actual = sut.generateJwt(user);

        //THEN
        String token = actual.getToken();
        assertTrue(Objects.nonNull(token) && StringUtils.isNotBlank(token));

        List<String> tokenParts = Arrays.asList(token.split("\\."));
        assertEquals(3, tokenParts.size());

        String jwtHeaders = "{\"alg\":\"HS512\"}";
        assertEquals(encode(jwtHeaders), tokenParts.get(0));

        String jwtPayload = "{\"sub\":\"1\",\"iss\":\"issuer\",\"role\":\"ROLE_STUDENT\",\"exp\":1600000000}";
        assertEquals(encode(jwtPayload), tokenParts.get(1));
    }

    @Test
    void generateJwt_verifyGetDefaultTokenExpirationDate() {
        //GIVEN
        var jwtProperties = mock(JwtProperties.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(null, jwtProperties, null, null, null, null));
        doCallRealMethod().when(sut).generateJwt(any());

        when(sut.getDefaultTokenExpirationDate()).thenReturn(new Date(1600000000000L));

        when(jwtProperties.getKey()).thenReturn(getTestSecretKey());
        when(jwtProperties.getIssuer()).thenReturn("issuer");

        var user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Roles.ROLE_STUDENT);

        //WHEN
        sut.generateJwt(user);

        //THEN
        verify(sut).getDefaultTokenExpirationDate();
    }

    @Test
    void register_verifyException_whenUserWithUsernameDoesntExist() {
        //GIVEN
        var userService = mock(UserServiceImpl.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, null, null, null, null));
        doCallRealMethod().when(sut).register(any());

        when(userService.existsByUsername(any())).thenReturn(true);

        //THEN
        assertThrows(IllegalUserInputException.class, () -> sut.register(mock(UserCredentialDTO.class)));
    }

    @Test
    void register_verifyEncode() {
        //GIVEN
        var userService = mock(UserServiceImpl.class);
        var passwordEncoder = mock(PasswordEncoder.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, passwordEncoder, null, mock(UserMapper.class), null));
        doCallRealMethod().when(sut).register(any());

        when(userService.existsByUsername(any())).thenReturn(false);

        var userCredential = mock(UserCredentialDTO.class);
        when(userCredential.getPassword()).thenReturn("password");

        //WHEN
        sut.register(userCredential);

        //THEN
        verify(passwordEncoder).encode(eq("password"));
    }

    @Test
    void register_verifySaveUser() {
        //GIVEN
        var userService = mock(UserServiceImpl.class);
        var passwordEncoder = mock(PasswordEncoder.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, passwordEncoder, null, mock(UserMapper.class), null));
        doCallRealMethod().when(sut).register(any());

        when(userService.existsByUsername(any())).thenReturn(false);

        when(passwordEncoder.encode(any())).thenReturn("encoded");

        var userCredential = mock(UserCredentialDTO.class);
        when(userCredential.getUsername()).thenReturn("username");
        when(userCredential.getPassword()).thenReturn("password");

        //WHEN
        sut.register(userCredential);

        //THEN
        verify(userService).saveUser(argThat(arg -> arg.getUsername().equals("username") && arg.getPassword().equals("encoded")));
    }

    @Test
    void register_verifyToUserDTO() {
        //GIVEN
        var userService = mock(UserServiceImpl.class);
        var passwordEncoder = mock(PasswordEncoder.class);
        var userMapper = mock(UserMapper.class);
        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, passwordEncoder, null, userMapper, null));
        doCallRealMethod().when(sut).register(any());

        when(userService.existsByUsername(any())).thenReturn(false);

        when(passwordEncoder.encode(any())).thenReturn("encoded");

        var user = mock(User.class);
        when(userService.saveUser(any())).thenReturn(user);

        var userCredential = mock(UserCredentialDTO.class);
        when(userCredential.getUsername()).thenReturn("username");
        when(userCredential.getPassword()).thenReturn("password");

        //WHEN
        sut.register(userCredential);

        //THEN
        verify(userMapper).toUserDTO(eq(user));
    }

    @Test
    void getAuthenticatedUser_verifyGetUserById() {
        //GIVEN
        var authenticationProvider = mock(AuthenticationProvider.class);
        var userService = mock(UserServiceImpl.class);
        var userMapper = mock(UserMapper.class);

        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, null, authenticationProvider, userMapper, null));
        doCallRealMethod().when(sut).getAuthenticatedUser();

        var authentication = mock(AuthenticationDTO.class);
        when(authentication.getUserId()).thenReturn(1L);
        when(authenticationProvider.getAuthentication()).thenReturn(authentication);

        //WHEN
        sut.getAuthenticatedUser();

        //THEN
        verify(userService).getUserById(eq(1L));
    }

    @Test
    void getAuthenticatedUser_verifyToUserDTO() {
        //GIVEN
        var authenticationProvider = mock(AuthenticationProvider.class);
        var userService = mock(UserServiceImpl.class);
        var userMapper = mock(UserMapper.class);

        var sut = mock(SecurityServiceImpl.class, withConstructor(userService, null, null, authenticationProvider, userMapper, null));
        doCallRealMethod().when(sut).getAuthenticatedUser();

        var authentication = mock(AuthenticationDTO.class);
        when(authentication.getUserId()).thenReturn(1L);
        when(authenticationProvider.getAuthentication()).thenReturn(authentication);

        var user = mock(User.class);
        when(userService.getUserById(anyLong())).thenReturn(user);

        //WHEN
        sut.getAuthenticatedUser();

        //THEN
        verify(userMapper).toUserDTO(eq(user));
    }

    @Test
    void getAuthentication_verifyException_whenTokenIssuerIsNotValid() {
        //GIVEN
        var jwtParser = mock(JwtParser.class);
        var jwtProperties = mock(JwtProperties.class);

        var sut = mock(SecurityServiceImpl.class, withConstructor(null, jwtProperties, null, null, null, jwtParser));
        doCallRealMethod().when(sut).getAuthentication(anyString());

        var claims = mock(Claims.class);
        var jwt = mock(Jws.class);
        when(jwt.getBody()).thenReturn(claims);
        when(jwtParser.parseClaimsJws(anyString())).thenReturn(jwt);

        when(claims.getIssuer()).thenReturn("wrongIssuer");
        when(jwtProperties.getIssuer()).thenReturn("correctIssuer");

        //THEN
        assertThrows(IllegalUserInputException.class, () -> sut.getAuthentication("token"));
    }

    @Test
    void getAuthentication_checkResult() {
        //GIVEN
        var jwtParser = mock(JwtParser.class);
        var jwtProperties = mock(JwtProperties.class);

        var sut = mock(SecurityServiceImpl.class, withConstructor(null, jwtProperties, null, null, null, jwtParser));
        doCallRealMethod().when(sut).getAuthentication(anyString());

        var claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("subject");
        when(claims.getIssuer()).thenReturn("iss");

        var authority = mock(GrantedAuthority.class);
        when(sut.getRoles(any())).thenReturn(List.of(authority));

        var jwt = mock(Jws.class);
        when(jwt.getBody()).thenReturn(claims);
        when(jwtParser.parseClaimsJws(anyString())).thenReturn(jwt);
        when(jwtProperties.getIssuer()).thenReturn("iss");

        //WHEN
        final var actualResult = (UsernamePasswordAuthenticationToken) sut.getAuthentication("token");

        //THEN
        assertEquals("subject", actualResult.getPrincipal());
        assertEquals("", actualResult.getCredentials());
        assertEquals(List.of(authority), actualResult.getAuthorities());
    }

    @Test
    void getRoles_checkResult_whenThereIsNoRoleClaim() {
        //GIVEN
        var sut = mock(SecurityServiceImpl.class);
        doCallRealMethod().when(sut).getRoles(any());

        var claims = mock(Claims.class);

        //WHEN
        final List<GrantedAuthority> roles = sut.getRoles(claims);

        //THEN
        assertTrue(roles.isEmpty());
    }

    @Test
    void getRoles_checkResult() {
        //GIVEN
        var sut = mock(SecurityServiceImpl.class);
        doCallRealMethod().when(sut).getRoles(any());

        var claims = mock(Claims.class);
        when(claims.get(eq("role"))).thenReturn("ROLE_STUDENT,ROLE_ADMIN");

        //WHEN
        final List<GrantedAuthority> roles = sut.getRoles(claims);

        //THEN
        var expectedRoles = List.of(new SimpleGrantedAuthority("ROLE_STUDENT"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        assertEquals(expectedRoles, roles);
    }

    @Test
    void getDefaultTokenExpirationDate_checkResult() {
        //GIVEN
        var sut = mock(SecurityServiceImpl.class);
        doCallRealMethod().when(sut).getDefaultTokenExpirationDate();

        //WHEN
        final Date expirationDate = sut.getDefaultTokenExpirationDate();

        //THEN
        assertNotNull(expirationDate);
        assertNotNull(expirationDate.getTime());
    }

    private MockSettings withConstructor(UserService userService, JwtProperties jwtProperties, PasswordEncoder passwordEncoder,
                                         AuthenticationProvider authenticationProvider, UserMapper userMapper, JwtParser jwtParser) {
        return withSettings().useConstructor(userService, jwtProperties, passwordEncoder, authenticationProvider, userMapper, jwtParser);
    }

    private String getTestSecretKey() {
        return "promotionProjectKeypromotionProjectKeypromotionProjectKeypromotionProjectKeypromotionProjectKey";
    }

    private String encode(String input) {
        return Encoders.BASE64URL.encode(input.getBytes(StandardCharsets.UTF_8));
    }

}