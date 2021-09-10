package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByUsername(final String username);

    @Override
    Collection<User> findAllById(Iterable<Long> iterable);
}
