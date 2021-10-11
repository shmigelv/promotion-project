package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.model.Instructor;
import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.repository.InstructorRepository;
import com.shmigel.promotionproject.repository.StudentRepository;
import com.shmigel.promotionproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockSettings;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Test
    void getInstructorById_checkResult() {
        //GIVEN
        var instructorRepository = mock(InstructorRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(null, instructorRepository, null, null));
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
        var sut = mock(UserServiceImpl.class, withConstructor(null, instructorRepository, null, null));
        doCallRealMethod().when(sut).getInstructorById(anyLong());

        when(instructorRepository.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThrows(EntityNotFoundException.class, () -> sut.getInstructorById(1L));
    }

    @Test
    void getStudentById_checkResult() {
        //GIVEN
        var studentRepository = mock(StudentRepository.class);
        var sut = mock(UserServiceImpl.class, withConstructor(null, null, studentRepository, null));
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
        var sut = mock(UserServiceImpl.class, withConstructor(null, null, studentRepository, null));
        doCallRealMethod().when(sut).getStudentById(anyLong());

        when(studentRepository.findById(anyLong())).thenReturn(Optional.empty());

        //THEN
        assertThrows(EntityNotFoundException.class, () -> sut.getStudentById(1L));
    }



    private MockSettings withConstructor(UserRepository userRepository, InstructorRepository instructorRepository,
                                         StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        return withSettings().useConstructor(userRepository, instructorRepository, studentRepository, passwordEncoder);
    }

}