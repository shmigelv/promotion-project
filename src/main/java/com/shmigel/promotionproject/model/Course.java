package com.shmigel.promotionproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Course {

    @Id
    @GeneratedValue(generator = "courses_id_seq")
    private Long id;

    private String title;

    @ManyToMany
    @JoinTable(
            name = "instructor_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Collection<User> instructors;

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Collection<User> students;

    @OneToMany
    private Collection<Lesson> lessons;

}
