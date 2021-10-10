package com.shmigel.promotionproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lessons")
@Data
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(generator = "lessons_id_seq")
    private Long id;

    private String title;

    @JoinColumn(name = "course_id")
    @ManyToOne
    private Course course;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "lesson_id")
    private Collection<Homework> homeworks = new ArrayList<>();

    public Lesson(String title) {
        this.title = title;
    }

    public Lesson(String title, Course course) {
        this.title = title;
        this.course = course;
    }

}
