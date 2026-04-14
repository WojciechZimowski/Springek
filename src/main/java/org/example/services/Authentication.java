//package org.example.services;
//
//import org.example.models.User;
//import org.example.repositories.IUserRepository;
//
//public class Authentication {
//    private static Hasher hasher = new Hasher();
//    private final IUserRepository userRepository;
//
//
//    public Authentication(IUserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//    public static String hashPassword(String password) {
//        return hasher.hash(password);
//    }
//
//    public User authenticate(String login, String password) {
//        User user = userRepository.getUser(login);
//        if(user!=null){
//            String hashedPassword = hashPassword(password);
//            if(hashedPassword.equals(user.getPassword())){
//                return user;
//            }
//        }
//        return null;
//
//    }
//
//
//
//
//}
