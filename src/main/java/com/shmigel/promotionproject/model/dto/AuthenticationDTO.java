package com.shmigel.promotionproject.model.dto;

import com.shmigel.promotionproject.model.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO: rename
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthenticationDTO {

    private Long userId;
    private String password;
    private Roles role;

}
