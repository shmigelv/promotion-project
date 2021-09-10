package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.CourseFeedback;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;

import java.util.Collection;

public interface CourseService {

    void addStudentToCourse(Long userId, Long courseId);

    Course getCourseById(Long courseId);

    Collection<Course> getUserCourses();

    Collection<Course> getStudentCourses(Long userId);

    Collection<User> getCourseStudents(Long courseId);

    Course createCourse(CreateCourseDTO createCourseDTO);

    void assignInstructorToCourse(Long userId, Long courseId);

}
