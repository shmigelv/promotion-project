package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.dto.CourseDTO;
import com.shmigel.promotionproject.model.dto.CourseStatusDTO;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.model.dto.UserDTO;

import java.util.Collection;

public interface CourseService {

    void addStudentToCourse(Long courseId);

    Course getCourseById(Long courseId);

    Collection<Course> getUserCourses();

    Collection<CourseDTO> getMappedUserCourses();

    Collection<Course> getStudentCourses(Long userId);

    Collection<UserDTO> getCourseStudents(Long courseId);

    CourseDTO createCourse(CreateCourseDTO createCourseDTO);

    void assignInstructorToCourse(Long userId, Long courseId);

    CourseStatusDTO getCourseStatus(Long courseId);

    boolean isUserSubscribedToCourse(Long courseId, Long studentId);

}
