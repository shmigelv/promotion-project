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
import com.shmigel.promotionproject.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static java.util.Objects.isNull;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final InstructorRepository instructorRepository;

    private final StudentRepository studentRepository;

    public UserServiceImpl(UserRepository userRepository, InstructorRepository instructorRepository,
                           StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.instructorRepository = instructorRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Instructor getInstructorById(Long instructorId) {
        return instructorRepository.findById(instructorId)
                .orElseThrow(() -> new EntityNotFoundException("Instructor with id: " + instructorId + " is not found"));
    }

    @Override
    public Student getStudentById(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student with id: " + studentId + " is not found"));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username: " + username + " not found"));
    }

    @Override
    public Collection<Instructor> getAllInstructors(Collection<Long> instructorIds) {
        return instructorRepository.findAllById(instructorIds);
    }

    @Override
    public void setUserRole(Long userId, String roleName) {
        User user = getUserById(userId);

        if (Objects.nonNull(user.getRole())) {
            throw new IllegalUserInputException("User with id: " + userId + " already has role assigned");
        }

        if (isNull(Roles.fromValue(roleName))) {
            throw new IllegalUserInputException("Role with name " + roleName + " doesn't exist, available values " + Arrays.toString(Roles.values()));
        }

        if (isNull(Roles.fromValue(roleName))) {
            throw new IllegalUserInputException("Provided role: " + roleName + " is not valid, valid values: " + Arrays.toString(Roles.values()));
        }
        userRepository.updateUserRole(user.getId(), roleName);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
