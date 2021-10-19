package com.shmigel.promotionproject.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.Collection;

@Entity
@DiscriminatorValue("ROLE_INSTRUCTOR")
public class Instructor extends User {

    @OneToMany
    @JoinColumn(name = "instructor_id")
    private Collection<Course> courses;

}
