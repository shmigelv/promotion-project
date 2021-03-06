package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.config.properties.JwtProperties;
import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.exception.IllegalUserInputException;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.model.dto.JwtDTO;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.dto.UserDTO;
import com.shmigel.promotionproject.model.mapper.UserMapper;
import com.shmigel.promotionproject.service.SecurityService;
import com.shmigel.promotionproject.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class SecurityServiceImpl implements SecurityService {

    private final UserService userService;

    private final JwtProperties jwtProperties;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationProvider authenticationProvider;

    private final UserMapper userMapper;

    private final JwtParser jwtParser;

    private static final String ROLE_CLAIM = "role";

    public SecurityServiceImpl(UserService userService, JwtProperties jwtProperties, PasswordEncoder passwordEncoder,
                               AuthenticationProvider authenticationProvider, UserMapper userMapper, JwtParser jwtParser) {
        this.userService = userService;
        this.jwtProperties = jwtProperties;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProvider = authenticationProvider;
        this.userMapper = userMapper;
        this.jwtParser = jwtParser;
    }

    @Override
    public JwtDTO login(UserCredentialDTO loginRequest) {
        User user = userService.getUserByUsername(loginRequest.getUsername());

        if (isNull(user) || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new EntityNotFoundException("Can't find user for given credentials");
        }

        return generateJwt(user);
    }

    protected JwtDTO generateJwt(User user) {
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
            throw new IllegalUserInputException("User with given username already exists");
        }

        User newUser = new User(userCredentialDTO.getUsername(), passwordEncoder.encode(userCredentialDTO.getPassword()));
        return userMapper.toUserDTO(userService.saveUser(newUser));
    }

    @Override
    public UserDTO getAuthenticatedUser() {
        Long userId = authenticationProvider.getAuthentication().getUserId();
        User authenticatedUser = userService.getUserById(userId);
        return userMapper.toUserDTO(authenticatedUser);
    }

    @Override
    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        String issuer = claims.getIssuer();

        if (!jwtProperties.getIssuer().equals(issuer)) {
            throw new IllegalUserInputException("Token contains wrong issuer");
        }

        String principal = claims.getSubject();
        List<GrantedAuthority> authorities = getRoles(claims);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    protected List<GrantedAuthority> getRoles(Claims claims) {
        if (isNull(claims.get(ROLE_CLAIM))) {
            return List.of();
        }

        return Arrays.stream(claims.get(ROLE_CLAIM).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    protected Date getDefaultTokenExpirationDate() {
        long currentTimeMillis = System.currentTimeMillis() + 1_800_000_0;
        return new Date(currentTimeMillis);
    }
}
