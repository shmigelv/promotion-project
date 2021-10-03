package com.shmigel.promotionproject.service.impl;

import com.shmigel.promotionproject.model.Admin;
import com.shmigel.promotionproject.repository.AdminRepository;
import com.shmigel.promotionproject.service.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends AbstractCrudService<Admin, Long> {

    private AdminRepository adminRepository;

    public AdminServiceImpl(AdminRepository adminRepository) {
        super(adminRepository);
    }
}
