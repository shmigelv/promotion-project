package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Instructor;
import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.model.User;

import java.util.Collection;

public interface UserService {

    User saveUser(User user);

    User getUserById(Long userId);

    User getUserByUsername(String username);

    Collection<Instructor> getAllInstructors(Collection<Long> instructorIds);

    Instructor getInstructorById(Long instructorId);

    Student getStudentById(Long studentId);

    void setUserRole(Long userId, String role);

    boolean existsByUsername(String username);
}
