package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    User getUserById(Long userId);

    User getUserByUsername(String username);

    User findByIdAndRole(Long userId, Roles role);

    Collection<User> findAllByIdsAndRole(Collection<Long> userIds, Roles role);

    User saveUser(String username, String password);

    void setUserRole(Long userId, String role);

}
