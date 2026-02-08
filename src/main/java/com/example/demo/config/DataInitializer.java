package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user if not exists
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("admin123"));
                admin.addRole("ADMIN");
                admin.addRole("USER");
                userRepository.save(admin);
                System.out.println("✅ Admin user created: username=admin, password=admin123");
            }

            // Create regular user if not exists
            if (!userRepository.existsByUsername("user")) {
                User user = new User("user", "user@example.com", passwordEncoder.encode("user123"));
                user.addRole("USER");
                userRepository.save(user);
                System.out.println("✅ Regular user created: username=user, password=user123");
            }
        };
    }
}