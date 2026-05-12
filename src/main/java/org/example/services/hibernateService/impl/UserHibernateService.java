package org.example.services.hibernateService.impl;

import org.example.models.User;
import org.example.services.hibernateService.UserServiceInterface;

import java.util.List;

public class UserHibernateService implements UserServiceInterface {
    @Override
    public List<User> findAllUsers() {
        return List.of();
    }

    @Override
    public User findById(String id) {
        return null;
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {

    }
}
