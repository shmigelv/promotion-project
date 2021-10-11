package com.shmigel.promotionproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter @Setter
@DiscriminatorValue("ROLE_INSTRUCTOR")
public class Instructor extends User {

    @ManyToMany(mappedBy = "instructors")
    private Collection<Course> courses = new ArrayList<>();

    public Instructor() {
    }

    public Instructor(String username, String password) {
        super(username, password, Roles.ROLE_INSTRUCTOR);
    }
}
