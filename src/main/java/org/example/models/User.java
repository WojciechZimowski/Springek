package org.example.models;

import lombok.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;
import java.util.function.Function;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private static final long serialVersionUID = 1L;
    private String id;
    private String login;
    private String password;
    private Role role;




    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }
    public User( String id,String login, String password, String role) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = Role.valueOf(role.toUpperCase());


    }
    public User copy(){
        return User.builder().id(id).login(login).password(password).role(role).build();

    }

    public String toString(){
        return String.format("Użytkownik: %s | Rola: %s",login,role);
    }


    public String getLogin() {
        return login;
    }

}

