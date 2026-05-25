package org.example.services.jdbcService;

import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.example.repositories.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
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
    public User findUserByLogin(String login) {
        return userJsonRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje w bazie."));
    }

}
