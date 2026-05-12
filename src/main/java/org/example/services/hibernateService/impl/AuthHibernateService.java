package org.example.services.hibernateService.impl;

import org.example.models.User;
import org.example.services.hibernateService.AuthServiceInterface;

import java.util.Optional;

public class AuthHibernateService implements AuthServiceInterface {
    @Override
    public boolean register(String login, String rawPassword) {
        return false;
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        return Optional.empty();
    }
//ee
}

