package org.example.repositories.hibernate;

import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.example.repositories.hibernate.jpa.IUserJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class UserJpaRepository implements IUserRepository {
    private final IUserJpaRepository userJpaRepository;

    public UserJpaRepository(IUserJpaRepository userJpaRepository) {
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
        return userJpaRepository.findByLogin(login);
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
