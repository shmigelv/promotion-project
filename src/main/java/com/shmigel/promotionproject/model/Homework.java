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

    @ManyToOne
    private User student;

    @ManyToOne
    private Lesson lesson;

    private Integer mark;

    private String homeworkFileKey;

}
