package org.example.controllers;

import org.example.models.User;
import org.example.services.hibernateService.UserServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserServiceInterface userService;

    public UserController(UserServiceInterface userService) {
        this.userService = userService;
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
