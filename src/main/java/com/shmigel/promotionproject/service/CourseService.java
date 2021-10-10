package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.dto.*;

import java.util.Collection;
import java.util.List;

public interface CourseService {

    void addStudentToCourse(Long userId, Long courseId);

    Course getCourseById(Long courseId);

    Collection<Course> getUserCourses();

    Collection<Course> getStudentCourses(Long userId);

    Collection<UserDTO> getCourseStudents(Long courseId);

    CourseDTO createCourse(CreateCourseDTO createCourseDTO);

    void assignInstructorToCourse(Long userId, Long courseId);

    CourseStatusDTO getCourseStatus(Long studentId, Long courseId);

    boolean isUserSubscribedToCourse(Long courseId, Long studentId);

}
