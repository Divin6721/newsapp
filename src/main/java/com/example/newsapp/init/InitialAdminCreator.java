package com.example.newsapp.init;

import com.example.newsapp.model.Role;
import com.example.newsapp.model.User;
import com.example.newsapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitialAdminCreator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = User.builder()
                    .name("SuperAdmin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .isBlocked(false)
                    .build();

            userRepository.save(admin);
            System.out.println("✅ Админ создан: admin@example.com / admin123");
        }
    }
}
