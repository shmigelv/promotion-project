package com.shmigel.promotionproject.controller;

import com.shmigel.promotionproject.model.dto.UserDTO;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final SecurityService securityService;

    @Autowired
    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/role")
    public ResponseEntity<Void> assignRoleForUser(@PathVariable Long userId, @RequestBody String role) {
        userService.setUserRole(userId, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> currentUser() {
        return ResponseEntity.ok(securityService.getAuthenticatedUser());
    }

}
