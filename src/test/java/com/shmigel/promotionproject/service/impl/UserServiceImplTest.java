package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.exception.IllegalUserInputException;
import com.shmigel.promotionproject.model.Instructor;
import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.repository.InstructorRepository;
import com.shmigel.promotionproject.repository.StudentRepository;
import com.shmigel.promotionproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Test
    void getInstructorById_checkResult() {
        //GIVEN
        var instructorRepository = mock(InstructorRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(null, instructorRepository, null));
        doCallRealMethod().when(sut).getInstructorById(anyLong());

        var instructor = mock(Instructor.class);
        when(instructorRepository.findById(anyLong())).thenReturn(Optional.of(instructor));

        //WHEN
        Instructor actualInstructor = sut.getInstructorById(1L);

        //THEN
        assertSame(instructor, actualInstructor);
    }

    @Test
    void getInstructorById_verifyException_whenInstructorDoesntExistForGivenId() {
        //GIVEN
        var instructorRepository = mock(InstructorRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(null, instructorRepository, null));
        doCallRealMethod().when(sut).getInstructorById(anyLong());

        when(instructorRepository.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThrows(EntityNotFoundException.class, () -> sut.getInstructorById(1L));
    }

    @Test
    void getStudentById_checkResult() {
        //GIVEN
        var studentRepository = mock(StudentRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(null, null, studentRepository));
        doCallRealMethod().when(sut).getStudentById(anyLong());

        var student = mock(Student.class);
        when(studentRepository.findById(anyLong())).thenReturn(Optional.of(student));

        //WHEN
        Student actualStudent = sut.getStudentById(1L);

        //THEN
        assertSame(student, actualStudent);
    }

    @Test
    void getStudentById_verifyException_whenStudentDoesntExistForGivenId() {
        //GIVEN
        var studentRepository = mock(StudentRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(null, null, studentRepository));
        doCallRealMethod().when(sut).getStudentById(anyLong());

        //WHEN
        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThrows(EntityNotFoundException.class, () -> sut.getStudentById(1L));
    }

    @Test
    void saveUser_checkResult() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).saveUser(any());

        var savedUser = mock(User.class);
        when(userRepository.save(any())).thenReturn(savedUser);

        //WHEN
        var inputUser = mock(User.class);
        User actual = sut.saveUser(inputUser);

        //THEN
        assertSame(savedUser, actual);
    }

    @Test
    void getUserById_checkResult_whenUserExists() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).getUserById(any());

        var savedUser = mock(User.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(savedUser));

        //WHEN
        User actual = sut.getUserById(1L);

        //THEN
        assertSame(savedUser, actual);
    }

    @Test
    void getUserById_verifyException_whenUserDoesntExist() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).getUserById(any());

        var savedUser = mock(User.class);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThrows(EntityNotFoundException.class, () -> sut.getUserById(1L));
    }

    @Test
    void getUserByUsername_checkResult_whenUserExists() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).getUserByUsername(any());

        var savedUser = mock(User.class);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.of(savedUser));

        //WHEN
        User actual = sut.getUserByUsername("username");

        //THEN
        assertSame(savedUser, actual);
    }

    @Test
    void getUserByUsername_verifyException_whenUserDoesntExist() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).getUserByUsername(any());

        var savedUser = mock(User.class);
        when(userRepository.findUserByUsername(anyString())).thenReturn(Optional.empty());

        //THEN
        assertThrows(EntityNotFoundException.class, () -> sut.getUserByUsername("username"));
    }

    @Test
    void getAllInstructors_checkResult() {
        //GIVEN
        var instructorRepository = mock(InstructorRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(null, instructorRepository, null));
        doCallRealMethod().when(sut).getAllInstructors(anyCollection());

        var instructor = mock(Instructor.class);
        when(instructorRepository.findAllById(anyCollection())).thenReturn(List.of(instructor));

        //WHEN
        Collection<Instructor> actualInstructors = sut.getAllInstructors(List.of(1L));

        //THEN
        assertEquals(List.of(instructor), actualInstructors);
    }

    @Test
    void getAllInstructors_verifyException_whenInstructorDoesntExistForGivenId() {
        //GIVEN
        var instructorRepository = mock(InstructorRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(null, instructorRepository, null));
        doCallRealMethod().when(sut).getAllInstructors(anyCollection());

        when(instructorRepository.findAllById(anyCollection())).thenReturn(List.of());

        //THEN
        assertThrows(EntityNotFoundException.class, () -> sut.getAllInstructors(List.of(1L)));
    }

    @Test
    void setUserRole_verifyUpdateUserRole_whenUserExistsWithoutRole() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).setUserRole(anyLong(), anyString());

        var user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(null);

        when(sut.getUserById(anyLong())).thenReturn(user);

        //WHEN
        sut.setUserRole(1L, Roles.ROLE_STUDENT.name());

        //THEN
        verify(userRepository).updateUserRole(eq(1L), eq(Roles.ROLE_STUDENT.name()));
    }

    @Test
    void setUserRole_verifyException_whenUserDoesntExist() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).setUserRole(anyLong(), anyString());
        doCallRealMethod().when(sut).getUserById(anyLong());

        var user = mock(User.class);
        when(user.getId()).thenReturn(1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        var actualException = assertThrows(EntityNotFoundException.class, () -> sut.setUserRole(1L, Roles.ROLE_STUDENT.name()));
        assertEquals("User with id: 1 not found", actualException.getMessage());
    }

    @Test
    void setUserRole_verifyException_whenUserHasRoleSet() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).setUserRole(anyLong(), anyString());

        var user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getRole()).thenReturn(Roles.ROLE_STUDENT);

        when(sut.getUserById(anyLong())).thenReturn(user);

        //THEN
        var actualException = assertThrows(IllegalUserInputException.class, () -> sut.setUserRole(1L, Roles.ROLE_STUDENT.name()));
        assertEquals("User with id: 1 already has role assigned", actualException.getMessage());
    }

    @Test
    void setUserRole_verifyException_whenInvalidRoleProvidedInArguments() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).setUserRole(anyLong(), anyString());

        when(sut.getUserById(anyLong())).thenReturn(mock(User.class));

        //WHEN
        var actualException = assertThrows(IllegalUserInputException.class, () -> sut.setUserRole(1L, "invalid_role"));

        //THEN
        assertEquals("Role with name invalid_role doesn't exist, available values "+ Arrays.toString(Roles.values()),
                actualException.getMessage());
    }

    @Test
    void existsByUsername_checkResult() {
        //GIVEN
        var userRepository = mock(UserRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(userRepository, null, null));
        doCallRealMethod().when(sut).existsByUsername(anyString());

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //WHEN
        boolean actualResult = sut.existsByUsername("username");

        //THEN
        assertTrue(actualResult);
    }

    private MockSettings withConstructor(UserRepository userRepository, InstructorRepository instructorRepository,
                                         StudentRepository studentRepository) {
        return withSettings().useConstructor(userRepository, instructorRepository, studentRepository);
    }

}