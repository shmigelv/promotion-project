package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.exception.EntityNotFoundException;
import com.shmigel.promotionproject.exception.IlligalUserInputException;
import com.shmigel.promotionproject.model.Roles;
import com.shmigel.promotionproject.model.User;
import com.shmigel.promotionproject.repository.UserRepository;
import com.shmigel.promotionproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(String username, String password) {
        return userRepository.save(new User(username, passwordEncoder.encode(password)));
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username: " + username + " not found"));
    }

    @Override
    public Collection<User> findAllByIdsAndRole(Collection<Long> userIds, Roles role) {
        Collection<User> users = userRepository.findAllById(userIds);

        for (User user : users) {
            if (!role.equals(user.getRole())) {
                throw new IlligalUserInputException("User with id: " + user.getId() + " dont have expected role: " + role);
            }
        }

        return users;
    }

    @Override
    public User findByIdAndRole(Long userId, Roles role) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with id: " + userId + " is not found");
        }

        if (Objects.isNull(user.get().getRole())) {
            throw new EntityNotFoundException("User with id: " + userId + " has different role");
        }

        return user.get();
    }

    @Override
    public void setUserRole(Long userId, String roleName) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new EntityNotFoundException("User with id: " + userId + " is not found");
        }

        if (Objects.nonNull(user.get().getRole())) {
            throw new EntityNotFoundException("User with id: " + userId + " already has role assigned");
        }

        user.get().setRole(Roles.fromValue(roleName));
        userRepository.save(user.get());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
