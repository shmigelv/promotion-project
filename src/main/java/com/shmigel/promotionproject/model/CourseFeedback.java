package com.shmigel.promotionproject.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Table(name = "course_feedbacks")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseFeedback {

    @Id
    @GeneratedValue(generator = "course_feedbacks_id_seq")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private String feedback;

    public CourseFeedback(Course course, User student, String feedback) {
        this.course = course;
        this.student = student;
        this.feedback = feedback;
    }
}
