package com.shmigel.promotionproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
@DiscriminatorValue("null")
@DiscriminatorOptions(insert = false)
public class User {

    @Id
    @GeneratedValue(generator = "users_id_seq")
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(insertable = false, nullable = false)
    private Roles role;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, Roles role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }
}
