package org.example.repositories.impl;

import org.example.db.JsonFileStorage;
import org.example.repositories.IUserRepository;
import org.example.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserJsonRepository implements IUserRepository {
    private final JsonFileStorage<User> jsonFileStorage;
    private static List<User> users=new ArrayList<>();

    public UserJsonRepository(JsonFileStorage<User> jsonFileStorage) {
        this.jsonFileStorage = jsonFileStorage;
        this.users = jsonFileStorage.load();
    }




    @Override
    public List<User> findAll() {
        this.users = jsonFileStorage.load();
        return this.users.stream().map(User::copy).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(String id) {
        return jsonFileStorage.load().stream().filter(u->u.getId().equals(id)).findFirst().map(User::copy);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return jsonFileStorage.load().stream().filter(u->u.getLogin().equals(login)).findFirst().map(User::copy);
    }

    @Override
    public User save(User user) {
    this.users=jsonFileStorage.load();
    this.users.removeIf(u->u.getId().equals(user.getId()) ||  u.getLogin().equals(user.getLogin()));
    User copy=user.copy();
    this.users.add(copy);
    jsonFileStorage.save(this.users);

        return copy;
    }

    @Override
    public void deleteById(String id) {
        this.users=jsonFileStorage.load();
        this.users.removeIf(u->u.getId().equals(id));
        jsonFileStorage.save(users);
    }


}
