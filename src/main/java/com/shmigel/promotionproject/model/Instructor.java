package com.shmigel.promotionproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter @Setter
@DiscriminatorValue("ROLE_INSTRUCTOR")
public class Instructor extends User {

    @ManyToMany(mappedBy = "instructors")
    private Collection<Course> courses;

}
