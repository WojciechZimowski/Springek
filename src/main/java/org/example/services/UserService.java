package org.example.services;

import org.example.models.User;
import org.example.repositories.impl.RentalJsonRepository;
import org.example.repositories.impl.UserJsonRepository;

import java.util.List;

public class UserService {
    private final UserJsonRepository userJsonRepository;
    private final RentalJsonRepository rentalJsonRepository;

    public UserService(UserJsonRepository userJsonRepository, RentalJsonRepository rentalJsonRepository) {
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
}
