package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.GenericRestException;
import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.dto.AuthenticationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Objects.isNull;

@Component
public class AuthenticationProvider {

    public AuthenticationDTO getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isNull(authentication) || !authentication.isAuthenticated()) {
            throw new GenericRestException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        return AuthenticationDTO.builder()
                .userId(Long.valueOf(convert(authentication.getPrincipal(), String.class)))
                .role(mapToRole(authentication.getAuthorities()))
                .build();
    }

    private Roles mapToRole(Collection<? extends GrantedAuthority> authorities) {
        if (authorities.size() != 1) {
            throw new GenericRestException(HttpStatus.FORBIDDEN, "User doesn't have role set");
        }

        return Roles.fromValue(authorities.stream().findFirst().get().getAuthority());
    }

    private <T> T convert(Object value, Class<T> clazz) {
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        } else {
            throw new GenericRestException(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't extract user principal");
        }
    }

}
