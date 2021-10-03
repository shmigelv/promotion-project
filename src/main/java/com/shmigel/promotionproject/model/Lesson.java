package com.shmigel.promotionproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    @OneToMany
    @JoinColumn(name = "lesson_id")
    private List<Homework> homeworks;

    public Lesson(String title, Course course) {
        this.title = title;
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(title, lesson.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
