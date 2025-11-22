package com.example.demoSpring17.configuration;

import com.example.demoSpring17.enums.Role;
import com.example.demoSpring17.models.User;
import com.example.demoSpring17.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
  PasswordEncoder passwordEncoder;

  @Bean
  ApplicationRunner applicationRunner(UserRepository userRepository) {
    return args -> {
      if (userRepository.findByUsername("admin").isEmpty()) {
        userRepository.save(
            User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("admin"))
                .role(Role.ADMIN)
                .build()
        );
        log.warn("add default admin user with username: admin and password: admin");
      }
    };
  }
}
