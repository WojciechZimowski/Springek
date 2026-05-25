package org.example.repositories.hibernate;

import org.example.db.HibernateConfig;
import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.example.repositories.hibernate.jpa.UserJpaRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class UserHibernateRepository implements IUserRepository {
    private final UserJpaRepository userJpaRepository;

    public UserHibernateRepository(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userJpaRepository.findById(login);
    }

    @Override
    public User save(User user) {
        if (user.getId() == null || user.getId().isBlank()) {
            user.setId(UUID.randomUUID().toString());
        }
        return userJpaRepository.save(user);
    }

    @Override
    public void deleteById(String id) {
       userJpaRepository.deleteById(id);

    }


}
