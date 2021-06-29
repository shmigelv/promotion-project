package com.shmigel.promotionproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class TestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("test");
    }

    @GetMapping("/db_test")
    public ResponseEntity<String> testDb() {

        return ResponseEntity.ok("test");
    }

}
