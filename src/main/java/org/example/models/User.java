package org.example.models;

import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String id;
    private String login;
    private String passwordHash;
    private Role role;





    public User copy(){
        return User.builder().id(id).login(login).passwordHash(passwordHash).role(role).build();

    }

    public String toString(){
        return String.format("Użytkownik: %s | Rola: %s",login,role);
    }


    public String getLogin() {
        return login;
    }

}

