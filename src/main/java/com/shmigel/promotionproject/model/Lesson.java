package com.shmigel.promotionproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany
    private List<Homework> homeworks;

    public Lesson(String title, Course course) {
        this.title = title;
        this.course = course;
    }
}
