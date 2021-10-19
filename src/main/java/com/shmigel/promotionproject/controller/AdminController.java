package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.dto.CourseDTO;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    private final CourseService courseService;

    @Autowired
    public AdminController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<Void> assignRoleForUser(@PathVariable Long userId, @RequestBody String role) {
        userService.setUserRole(userId, role);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/courses")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CreateCourseDTO course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

}
