package com.shmigel.promotionproject.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Getter
@Setter
@DiscriminatorValue("ROLE_STUDENT")
public class Student extends User {

    @OneToMany
    @JoinColumn(name = "student_id")
    private Collection<Homework> homeworks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Collection<Course> courses = new ArrayList<>();

    public Student() {
    }

    public Student(String username, String password) {
        super(username, password, Roles.ROLE_STUDENT);
    }
}
