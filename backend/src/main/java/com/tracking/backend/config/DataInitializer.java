package com.tracking.backend.config;

import com.tracking.backend.entity.Role;
import com.tracking.backend.entity.User;
import com.tracking.backend.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // ==========================================
            // CREATE ADMIN USER
            // ==========================================

            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = new User();

                admin.setUsername("admin");

                admin.setPassword(
                        passwordEncoder.encode("admin123")
                );

                admin.setRole(Role.ROLE_ADMIN);

                userRepository.save(admin);

                System.out.println(
                        "ADMIN USER CREATED"
                );
            }


            // ==========================================
            // CREATE NORMAL USER
            // ==========================================

            if (userRepository.findByUsername("user").isEmpty()) {

                User user = new User();

                user.setUsername("user");

                user.setPassword(
                        passwordEncoder.encode("user123")
                );

                user.setRole(Role.ROLE_USER);

                userRepository.save(user);

                System.out.println(
                        "NORMAL USER CREATED"
                );
            }
        };
    }
}