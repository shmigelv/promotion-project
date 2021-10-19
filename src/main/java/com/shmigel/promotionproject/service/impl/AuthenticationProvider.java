package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

import static java.util.Objects.isNull;

public class AuthenticationProvider {

    public static AuthenticationDTO getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication) || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not any user is authenticated");
        }

        return AuthenticationDTO.builder()
                .userId(Long.valueOf(convert(authentication.getPrincipal(), String.class)))
                .role(mapToRole(authentication.getAuthorities()))
                .build();
    }

    private static Roles mapToRole(Collection<? extends GrantedAuthority> authorities) {
        if (authorities.size() != 1) {
            throw new RuntimeException("Role is not set");
        }

        return Roles.fromValue(authorities.stream().findFirst().get().getAuthority());
    }

    private static <T> T convert(Object value, Class<T> clazz) {
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            throw new RuntimeException("Coudn't ");
        }
    }

}
