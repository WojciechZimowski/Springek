package org.example.controllers;

import org.example.models.User;
import org.example.repositories.IUserRepository;
import org.example.services.hibernateService.UserServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceInterface userService;
    private final IUserRepository userRepository;
    public UserController(UserServiceInterface userService, IUserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(UUID.randomUUID().toString());
        }
        if (userRepository.findById(user.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(java.util.Map.of("error", "Użytkownik o podanym ID już istnieje!"));
        }
        User savedUser = userRepository.save(user); // Zapis bezpośrednio przez repo
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {

        User foundUser = userService.findById(user.getId());

        if (foundUser != null && foundUser.getPasswordHash().equals(user.getPasswordHash())) {
            return ResponseEntity.ok("Zalogowano pomyślnie");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Błędne dane logowania");
    }

    @GetMapping
    public List<User> list() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable String id) {
        return userService.findById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestHeader(name = "X-Logged-User-Id", required = false) String loggedUserId
    ) {
        userService.deleteUser(id, loggedUserId);
        return ResponseEntity.noContent().build();
    }
}
