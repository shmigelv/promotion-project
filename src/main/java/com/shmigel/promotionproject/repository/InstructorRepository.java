package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.Instructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface InstructorRepository extends CrudRepository<Instructor, Long> {

    Collection<Instructor> findAllById(Iterable<Long> ids);

}
