package com.shmigel.promotionproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseFeedback {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Course course;

    @ManyToOne
    private User student;

    private String feedback;

    public CourseFeedback(Course course, User student, String feedback) {
        this.course = course;
        this.student = student;
        this.feedback = feedback;
    }
}
