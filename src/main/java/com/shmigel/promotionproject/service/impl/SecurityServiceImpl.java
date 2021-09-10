package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.config.properties.JwtProperties;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SecurityServiceImpl implements SecurityService {

    private UserService userService;

    private JwtProperties jwtProperties;

    private AuthenticationProvider authenticationProvider;

    private static final String ROLE_CLAIM = "role";

    public SecurityServiceImpl(UserService userService, JwtProperties jwtProperties, AuthenticationProvider authenticationProvider) {
        this.userService = userService;
        this.jwtProperties = jwtProperties;
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    @Transactional
    public String login(UserCredentialDTO loginRequest) {
        User user = userService.getUserByUsername(loginRequest.getUsername());

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuer(jwtProperties.getIssuer())
                .claim(ROLE_CLAIM, user.getRole())
                .setExpiration(getDefaultTokenExpirationDate())
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getKey())
                .compact();
    }

    @Override
    public User register(UserCredentialDTO userCredentialDTO) {
        return userService.saveUser(userCredentialDTO.getUsername(), userCredentialDTO.getPassword());
    }

    @Override
    public User getAuthenticatedUser() {
        Long userId = authenticationProvider.getAuthentication().getUserId();
        return userService.getUserById(userId);
    }

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtProperties.getKey())
                .parseClaimsJws(token)
                .getBody();

        String principal = claims.getSubject();
        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get(ROLE_CLAIM).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    private Date getDefaultTokenExpirationDate() {
        long currentTimeMillis = System.currentTimeMillis() + 1_800_000_0;
        return new Date(currentTimeMillis);
    }
}
