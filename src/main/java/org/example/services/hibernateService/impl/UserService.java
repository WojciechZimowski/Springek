package org.example.services.hibernateService.impl;

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

    public UserService(UserJpaRepository userRepository, RentalJpaRepository rentalRepository) {
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

        boolean hasActiveRental = rentalRepository.findByVehicleIdAndReturnDateIsNull(id).isPresent();

        if (hasActiveRental) {
            throw new IllegalStateException("Użytkownik posiada aktywne wypożyczenie!");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym ID: " + id));

        userRepository.deleteById(user.getId());
    }
}
