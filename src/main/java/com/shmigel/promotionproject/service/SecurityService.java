package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.dto.UserCredentialDTO;
import com.shmigel.promotionproject.model.User;
import org.springframework.security.core.Authentication;

public interface SecurityService {

    String login(UserCredentialDTO loginRequest);

    User register(final UserCredentialDTO userCredentialDTO);

    User getAuthenticatedUser();

    Authentication getAuthentication(String token);

}
