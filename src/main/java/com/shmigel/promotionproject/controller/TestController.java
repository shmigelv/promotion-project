package com.shmigel.promotionproject.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @GetMapping("/instructor")
    public ResponseEntity<String> testInstructor() {
        return ResponseEntity.ok("test");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> testAdmin() {
        return ResponseEntity.ok("test");
    }

}
