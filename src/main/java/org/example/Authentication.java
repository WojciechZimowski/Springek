package org.example;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;

public class Authentication {
    private static Hasher hasher = new Hasher();
    private final IUserRepository userRepository;


    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public static String hashPassword(String password) {
        return hasher.hash(password);
    }

    public User authenticate(String login, String password) {
        User user = userRepository.getUser(login);
        if(user!=null){
            String hashedPassword = hashPassword(password);
            if(hashedPassword.equals(user.getPassword())){
                return user;
            }
        }
        return null;
        
    }




}
