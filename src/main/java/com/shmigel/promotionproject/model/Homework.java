package com.shmigel.promotionproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "homeworks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Homework {

    @Id
    @GeneratedValue(generator = "homeworks_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    private Lesson lesson;

    private Integer mark;

    private String homeworkFileKey;

    @Override
    public String toString() {
        return "Homework{" +
                "id=" + id +
                ", mark=" + mark +
                ", homeworkFileKey='" + homeworkFileKey + '\'' +
                '}';
    }
}
