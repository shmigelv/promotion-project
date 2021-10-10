package com.shmigel.promotionproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@DiscriminatorValue("ROLE_STUDENT")
public class Student extends User {

    @OneToMany
    @JoinColumn(name = "student_id")
    private Collection<Homework> homeworks;

    @ManyToMany(mappedBy = "students")
    private Collection<Course> courses;

}
