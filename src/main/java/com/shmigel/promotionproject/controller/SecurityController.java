package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.dto.JwtDTO;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.UserDTO;
import com.shmigel.promotionproject.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class SecurityController {

    private SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDTO> login(@RequestBody UserCredentialDTO loginRequest) {
        return ResponseEntity.ok(securityService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody UserCredentialDTO loginRequest) {
        return ResponseEntity.ok(securityService.register(loginRequest));
    }

    @PostMapping("/me")
    public ResponseEntity<UserDTO> currentUser() {
        return ResponseEntity.ok(securityService.getAuthenticatedUser());
    }
}
