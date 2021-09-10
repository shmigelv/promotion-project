package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.repository.UserRepository;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findUser(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User saveUser(String username, String password) {
        return userRepository.save(new User(username, password));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + userId + " not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username: " + username + " not found"));
    }

    @Override
    public Collection<User> findAllByIdsAndRole(Collection<Long> userIds, Roles role) {
        Collection<User> users = userRepository.findAllById(userIds);

        for (User user : users) {
            if (!role.equals(user.getRole())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "User with id: " + user.getId() + " didn't match expected role: " + role);
            }
        }

        return users;
    }

    @Override
    public User findByIdAndRole(Long userId, Roles role) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + userId + " is not found");
        }

        if (Objects.isNull(user.get().getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with id: " + userId + " has different role: " + role);
        }

        return user.get();
    }

    @Override
    public void setUserRole(Long userId, String roleName) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + userId + " is not found");
        }

        if (Objects.nonNull(user.get().getRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id: " + userId + " already has role assigned");
        }

        user.get().setRole(Roles.fromValue(roleName));
        userRepository.save(user.get());
    }

}
