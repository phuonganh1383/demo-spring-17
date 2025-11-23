package com.example.demoSpring17.controllers;

import com.example.demoSpring17.enums.Role;
import com.example.demoSpring17.models.User;
import com.example.demoSpring17.services.UserService;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

  UserService userService;
  PasswordEncoder passwordEncoder;

  // --- 1. CREATE (POST) ---
  @PostMapping
  public User createUser(@RequestBody User user) {
//    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    String hashed = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashed);
    user.setRole(Role.USER);
    return userService.saveUser(user);
  }

  // --- 2. READ (GET All) ---
  @GetMapping
  public List<User> getAllUsers() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    log.info("username: {}", authentication.getName());
    authentication.getAuthorities()
            .forEach(grant -> log.info("grant: {}", grant.getAuthority()));
    return userService.getAllUsers();
  }

  // --- 2. READ (GET By ID) ---
  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
    User user = userService.getUserById(id).orElseThrow(
            () -> new RuntimeException("User not found with id: " + id)
    );
    return ResponseEntity.ok(user);
  }

  // --- 3. UPDATE (PUT) ---
  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
    User updatedUser = userService.updateUser(id, userDetails);
    return ResponseEntity.ok(updatedUser);
  }

  // --- 4. DELETE (DELETE) ---
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return ResponseEntity.noContent().build();
  }
}
