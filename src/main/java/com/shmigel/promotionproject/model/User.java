package com.shmigel.promotionproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "users_id_seq")
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Roles role;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
