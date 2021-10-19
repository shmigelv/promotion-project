package com.shmigel.promotionproject.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@DiscriminatorValue("ROLE_STUDENT")
public class Student extends User {

    @OneToMany
    @JoinColumn(name = "student_id")
    private List<Homework> homeworks;

    public List<Homework> getHomeworks() {
        return homeworks;
    }

    public void setHomeworks(List<Homework> homeworks) {
        this.homeworks = homeworks;
    }
}
