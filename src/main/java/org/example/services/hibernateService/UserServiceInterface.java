package org.example.services.hibernateService;



import org.example.models.User;

import java.util.List;

public interface UserServiceInterface {

    List<User> findAllUsers();

    User findById(String id);

    void deleteUser(String id, String loggedUserId);
}