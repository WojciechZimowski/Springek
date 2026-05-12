package org.example.services.impl;

import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.example.repositories.RentalRepository;
import org.example.services.UserServiceInterface;

import java.util.List;

public class UserService implements UserServiceInterface {
    private final IUserRepository userJsonRepository;
    private final RentalRepository rentalJsonRepository;

    public UserService(IUserRepository userJsonRepository, RentalRepository rentalJsonRepository) {
        this.userJsonRepository = userJsonRepository;
        this.rentalJsonRepository = rentalJsonRepository;
    }

    public void deleteUser(String userId) {

        boolean hasActiveRentals = rentalJsonRepository.findAll().stream()
                .anyMatch(r -> r.getUserId().equals(userId) && r.getReturnDateTime() == null);

        if (hasActiveRentals) {
            throw new RuntimeException("Nie można usunąć użytkownika z aktywnym wypożyczeniem!");
        }
        userJsonRepository.deleteById(userId);
    }

    public List<User> findAllUsers() { return userJsonRepository.findAll(); }

    @Override
    public User findById(String id) {
        return null;
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {

    }

    public User findUserByLogin(String login) {
        return userJsonRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje w bazie."));
    }

}
