package org.example.services;

import org.example.models.User;
import org.example.repositories.impl.UserJsonRepository;

import java.util.List;

public class UserService {
    private final UserJsonRepository userJsonRepository;

    public UserService(UserJsonRepository userJsonRepository) {
        this.userJsonRepository = userJsonRepository;
    }


    public boolean deleteUser(String id){
    }
    public List<User> findAllUsers(){
    }
    public User findByIdUser(String id){
    }
}
