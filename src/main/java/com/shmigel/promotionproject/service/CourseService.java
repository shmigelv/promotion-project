package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.CourseStatus;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.CourseDTO;
import com.shmigel.promotionproject.model.dto.CourseDetailsDTO;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;

import java.util.Collection;

public interface CourseService {

    void addStudentToCourse(Long userId, Long courseId);

    Course getCourseById(Long courseId);

    Collection<Course> getUserCourses();

    Collection<Course> getStudentCourses(Long userId);

    Collection<User> getCourseStudents(Long courseId);

    CourseDTO createCourse(CreateCourseDTO createCourseDTO);

    void assignInstructorToCourse(Long userId, Long courseId);

    CourseStatus getCourseStatus(Long studentId, Long courseId);

    boolean isUserSubscribedToCourse(Long courseId, Long studentId);

    CourseDetailsDTO getCourseDetails(Long courseId);
}
