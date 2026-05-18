package org.example.services.hibernateService.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.db.HibernateConfig;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.hibernate.UserHibernateRepository;
import org.example.services.hibernateService.AuthServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;
import java.util.UUID;

public class AuthHibernateService implements AuthServiceInterface {
    private final UserHibernateRepository userRepository;

    public AuthHibernateService(UserHibernateRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean register(String login, String rawPassword) {
        Transaction tx = null;
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            userRepository.setSession(session);

            if (userRepository.findByLogin(login).isPresent()) {
                return false;
            }

            User user = User.builder()
                    .id(UUID.randomUUID().toString())
                    .login(login)
                    .passwordHash(DigestUtils.sha256Hex(rawPassword))
                    .role(Role.USER)
                    .build();

            userRepository.save(user);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        try (Session session = HibernateConfig.getSessionFactory().openSession()) {
            userRepository.setSession(session);
            String hashed = DigestUtils.sha256Hex(rawPassword);
            return userRepository.findByLogin(login)
                    .filter(u -> u.getPasswordHash().equals(hashed));
        }
    }
//ee
}

