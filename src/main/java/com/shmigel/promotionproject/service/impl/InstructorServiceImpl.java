package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.Instructor;
import com.shmigel.promotionproject.repository.InstructorRepository;
import com.shmigel.promotionproject.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class InstructorServiceImpl extends AbstractCrudService<Instructor, Long> {

    private InstructorRepository instructorRepository;

    public InstructorServiceImpl(InstructorRepository instructorRepository) {
        super(instructorRepository);
    }
}
