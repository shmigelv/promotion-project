package com.shmigel.promotionproject.controller.filter;

import com.shmigel.promotionproject.service.SecurityService;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Log4j2
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final SecurityService securityService;

    public AuthenticationFilter(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {
        Optional<String> authenticationToken = getAuthenticationToken(httpServletRequest);

        authenticationToken
                .map(securityService::getAuthentication)
                .ifPresent(auth -> {
                    log.info("Authenticating user: "+auth.getPrincipal()+" and authorities: "+auth.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private Optional<String> getAuthenticationToken(HttpServletRequest request) {
        String authorizationHeaderValue = request.getHeader("Authorization");
        if (nonNull(authorizationHeaderValue) && authorizationHeaderValue.startsWith("Bearer ")) {
            return Optional.of(getTokenFromHeader(authorizationHeaderValue));
        } else {
            return Optional.empty();
        }
    }

    private String getTokenFromHeader(String authorizationHeaderValue) {
        return authorizationHeaderValue.substring(7);
    }

}
