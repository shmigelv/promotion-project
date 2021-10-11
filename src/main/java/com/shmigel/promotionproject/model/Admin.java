package com.shmigel.promotionproject.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("ROLE_ADMIN")
public class Admin extends User {

    public Admin(String username, String password) {
        super(username, password, Roles.ROLE_ADMIN);
    }

}
