package org.example.services.hibernateService.impl;

import org.example.models.Rental;
import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.example.repositories.RentalRepository;
import org.example.repositories.hibernate.RentalJpaRepository;
import org.example.repositories.hibernate.UserJpaRepository;
import org.example.services.hibernateService.UserServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class UserService implements UserServiceInterface {
    private final IUserRepository userRepository;
    private final RentalRepository rentalRepository;

    public UserService(IUserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }


    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));
    }

    @Override
    public void deleteUser(String id, String loggedUserId) {
        if (id.equals(loggedUserId)) {
            throw new IllegalArgumentException("Nie możesz usunąć samego siebie");
        }

        List<Rental> allRentals = rentalRepository.findAll();

        boolean hasActiveRental = allRentals.stream()
                .anyMatch(r -> r.getUserId().equals(id) && r.getReturnDate() == null);

        if (hasActiveRental) {
            throw new IllegalStateException("Użytkownik posiada aktywne wypożyczenie i nie może zostać usunięty!");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika"));

        userRepository.deleteById(user.getId());
    }
}
