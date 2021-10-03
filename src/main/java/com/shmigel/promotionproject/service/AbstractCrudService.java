package com.shmigel.promotionproject.service;

import com.shmigel.promotionproject.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

public class AbstractCrudService<T extends User, ID> {

    private CrudRepository<T, ID> domainRepository;

    public AbstractCrudService(CrudRepository<T, ID> domainRepository) {
        this.domainRepository = domainRepository;
    }

    public T save(T object) {
        return domainRepository.save(object);
    }

    public T getById(ID id) {
        return domainRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found"));
    }

}
