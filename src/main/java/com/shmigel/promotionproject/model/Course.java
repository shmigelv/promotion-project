package com.shmigel.promotionproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Course {

    @Id
    @GeneratedValue(generator = "courses_id_seq")
    private Long id;

    private String title;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "instructor_courses",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Collection<Instructor> instructors = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, mappedBy = "courses")
    private Collection<Student> students = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "course_id")
    private Collection<Lesson> lessons = new ArrayList<>();

    public Course(String title, Collection<Instructor> instructors) {
        this.title = title;
        this.instructors = instructors;
    }

    public Course(String title) {
        this.title = title;
    }

    public void addLesson(Lesson lesson) {
        this.getLessons().add(lesson);
        lesson.setCourse(this);
    }

    public void addStudent(Student student) {
        this.getStudents().add(student);
        student.getCourses().add(this);
    }

    public void addInstructor(Instructor instructor) {
        this.getInstructors().add(instructor);
        instructor.getCourses().add(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(title, course.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
