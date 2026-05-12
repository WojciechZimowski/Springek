package org.example.services.hibernateService;



import org.example.models.User;

import java.util.Optional;

public interface AuthServiceInterface {

    boolean register(String login, String rawPassword);

    Optional<User> login(String login, String rawPassword);
}