package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.dto.JwtDTO;
import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.dto.UserDTO;
import org.springframework.security.core.Authentication;

public interface SecurityService {

    JwtDTO login(UserCredentialDTO loginRequest);

    UserDTO register(final UserCredentialDTO userCredentialDTO);

    UserDTO getAuthenticatedUser();

    Authentication getAuthentication(String token);

}
