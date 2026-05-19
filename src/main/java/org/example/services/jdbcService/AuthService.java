package org.example.services.jdbcService;

import org.example.models.Role;
import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class AuthService {
    private final IUserRepository userRepo;

    public AuthService(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }
    public boolean register(String login,String password){
        if(userRepo.findByLogin(login).isPresent()){
            return false;
        }
        String hashedPassword=BCrypt.hashpw(password,BCrypt.gensalt());
        User newUser = User.builder().id(UUID.randomUUID().toString()).login(login).passwordHash(hashedPassword).role(Role.USER).build();

        userRepo.save(newUser);
        return true;
    }
    public Optional<User> login(String login, String password){
        return userRepo.findByLogin(login)
                .filter(u -> {
                    String hashInDb = u.getPasswordHash();
                    if (hashInDb == null) return false;
                    return BCrypt.checkpw(password, hashInDb);
                });}
}
