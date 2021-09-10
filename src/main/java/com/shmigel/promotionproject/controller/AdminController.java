package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.Course;
import com.shmigel.promotionproject.model.dto.CreateCourseDTO;
import com.shmigel.promotionproject.service.CourseService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<String> assignRoleForUser(@PathVariable Long userId, @RequestBody String role) {
        userService.setUserRole(userId, role);

        return ResponseEntity.ok("ROLE WAS SET");
    }

    @PostMapping("/courses")
    public ResponseEntity<Course> createCourse(@RequestBody CreateCourseDTO course) {
        return ResponseEntity.ok(courseService.createCourse(course));
    }

}
