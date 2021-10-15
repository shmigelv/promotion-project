package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByUsername(String username);
    
    Collection<User> findAllByUsername(String username);
    
    Collection<User> findAllById(Iterable<Long> iterable);

    boolean existsByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "update users set role = :role where id = :id", nativeQuery = true)
    void updateUserRole(Long id, String role);

}
