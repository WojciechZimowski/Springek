package org.example.services.hibernateService.impl;

import jakarta.transaction.Transactional;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.example.repositories.hibernate.UserJpaRepository;
import org.example.services.hibernateService.AuthServiceInterface;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
@Transactional
public class AuthService implements AuthServiceInterface {
    private final IUserRepository userRepository;

    public AuthService(UserJpaRepository userRepository) {
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

