package org.example.services.hibernateService.impl;

import jakarta.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.db.HibernateConfig;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.hibernate.UserHibernateRepository;
import org.example.services.hibernateService.AuthServiceInterface;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
@Profile("jpa")
@Transactional
public class AuthHibernateService implements AuthServiceInterface {
    private final UserHibernateRepository userRepository;

    public AuthHibernateService(UserHibernateRepository userRepository) {
        this.userRepository = userRepository;
    }
//entity manager aby działał ze springiem!!
    @Override
    public boolean register(String login, String rawPassword) {
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
        return true;
    }

    @Override
    public Optional<User> login(String login, String rawPassword) {
        String hashed = DigestUtils.sha256Hex(rawPassword);
        return userRepository.findByLogin(login)
                .filter(u -> u.getPasswordHash().equals(hashed));
    }
//ee
}

