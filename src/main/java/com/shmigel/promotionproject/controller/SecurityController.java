package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private final SecurityService securityService;

    public SecurityController(final SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserCredentialDTO loginRequest) {
        final String jwtToken = securityService.login(loginRequest);
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserCredentialDTO loginRequest) {
        final User loginRequestResponse = securityService.register(loginRequest);
        return ResponseEntity.ok(loginRequestResponse);
    }

    @PostMapping("/me")
    public ResponseEntity<User> currentUser() {
        return ResponseEntity.ok(securityService.getAuthenticatedUser());
    }
}
