package org.example.models;

import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "passwordHash")
@Table(name="users")
@EqualsAndHashCode(of="id")

public class User {
    @Id
    @Column(nullable = false,unique = true)
    @GeneratedValue(strategy=GenerationType.UUID)
    private String id;
    @Column(nullable = false,unique = true)
    private String login;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    private Role role;
    public User copy(){
        return User.builder().id(id).login(login).passwordHash(passwordHash).role(role).build();

    }



    public String getLogin() {
        return login;
    }

}

