package org.example.repositories.hibernate;

import org.example.models.User;
import org.example.repositories.IUserRepository;

import java.util.List;
import java.util.Optional;

public class UserHibernateRepository implements IUserRepository {
    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public void deleteById(String id) {

    }
}
