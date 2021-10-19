package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.Student;
import com.shmigel.promotionproject.repository.StudentRepository;
import com.shmigel.promotionproject.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends AbstractCrudService<Student, Long> {

    private StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        super(studentRepository);
    }
}
