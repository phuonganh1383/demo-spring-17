package com.example.demoSpring17.services;

import com.example.demoSpring17.models.User;
import com.example.demoSpring17.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

  UserRepository userRepository;

  // 1. CREATE & UPDATE (Save)
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  // 2. READ (Find All)
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  // 2. READ (Find By ID)
  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  // 3. UPDATE (Cần tìm kiếm và sau đó Save)
  public User updateUser(Long id, User userDetails) {
    User user = userRepository.findById(id).orElseThrow(
            () -> new RuntimeException("User not found with id: " + id)
    );
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
    String hashed = encoder.encode(userDetails.getPassword());
    user.setPassword(hashed);

    return userRepository.save(user);
  }

  // 4. DELETE
  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }
}
