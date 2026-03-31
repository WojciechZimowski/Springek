package org.example;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    boolean update(User user);
    boolean addUser(User user);
    boolean deleteUser(String login);
    //void save();
    //void load();

}