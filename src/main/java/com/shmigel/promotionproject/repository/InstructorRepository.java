package com.shmigel.promotionproject.repository;

import com.shmigel.promotionproject.model.Instructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorRepository extends CrudRepository<Instructor, Long> {
}
