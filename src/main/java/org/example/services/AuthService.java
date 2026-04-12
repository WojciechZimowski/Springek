package org.example.services;

import org.example.models.User;
import org.example.repositories.impl.UserRepository;

import java.util.Optional;

public class AuthService {
    UserRepository userRepo;
    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }
    public boolean register(String login,String password){

    }
    Optional<User> login(String login, String password){

    }
}
