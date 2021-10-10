package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.config.properties.JwtProperties;
import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.exception.IlligalUserInputException;
import com.shmigel.promotionproject.model.dto.JwtDTO;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.UserDTO;
import com.shmigel.promotionproject.model.mapper.UserMapper;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class SecurityServiceImpl implements SecurityService {

    private UserService userService;

    private JwtProperties jwtProperties;

    private PasswordEncoder passwordEncoder;

    private AuthenticationProvider authenticationProvider;

    private UserMapper userMapper;

    private static final String ROLE_CLAIM = "role";

    public SecurityServiceImpl(UserService userService, JwtProperties jwtProperties, PasswordEncoder passwordEncoder,
                               AuthenticationProvider authenticationProvider, UserMapper userMapper) {
        this.userService = userService;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public JwtDTO login(UserCredentialDTO loginRequest) {
        User user = userService.getUserByUsername(loginRequest.getUsername());

        if (isNull(user) || user.getPassword().equals(passwordEncoder.encode(loginRequest.getPassword()))) {
            throw new EntityNotFoundException("Can't find user for given credentials");
        }

        return generateJwt(user);
    }

    private JwtDTO generateJwt(User user) {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getKey()));

        Date tokenExpirationDate = getDefaultTokenExpirationDate();
        String token = Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuer(jwtProperties.getIssuer())
                .claim(ROLE_CLAIM, user.getRole())
                .setExpiration(tokenExpirationDate)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
        return new JwtDTO(token, tokenExpirationDate.getTime());
    }

    @Override
    public UserDTO register(UserCredentialDTO userCredentialDTO) {
        if (userService.existsByUsername(userCredentialDTO.getUsername())) {
            throw new IlligalUserInputException("User with given username already exists");
        }

        User createdUser = userService.saveUser(userCredentialDTO.getUsername(), userCredentialDTO.getPassword());
        return userMapper.toUserDTO(createdUser);
    }

    @Override
    public UserDTO getAuthenticatedUser() {
        Long userId = authenticationProvider.getAuthentication().getUserId();
        User authenticatedUser = userService.getUserById(userId);
        return userMapper.toUserDTO(authenticatedUser);
    }

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtProperties.getKey())
                .parseClaimsJws(token)
                .getBody();

        String principal = claims.getSubject();
        List<SimpleGrantedAuthority> authorities = getRoles(claims);

        if (authorities.isEmpty()) {
            return new UsernamePasswordAuthenticationToken(principal, "");
        } else {
            return new UsernamePasswordAuthenticationToken(principal, "", authorities);
        }
    }

    private List<SimpleGrantedAuthority> getRoles(Claims claims) {
        if (isNull(claims.get(ROLE_CLAIM))) {
            return List.of();
        }

        return Arrays.stream(claims.get(ROLE_CLAIM).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private Date getDefaultTokenExpirationDate() {
        long currentTimeMillis = System.currentTimeMillis() + 1_800_000_0;
        return new Date(currentTimeMillis);
    }
}
