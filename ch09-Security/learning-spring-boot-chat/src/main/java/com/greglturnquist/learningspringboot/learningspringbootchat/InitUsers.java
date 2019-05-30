package com.greglturnquist.learningspringboot.learningspringbootchat;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;

@Configuration
public class InitUsers {
    @Bean
    CommandLineRunner initializeUsers(MongoOperations operations) {
        return args -> {
            operations.dropCollection(User.class);

            operations.insert(
                    new User(
                            null,
                            "greg", "turnquist",
                            new String[]{"ROLE_USER", "ROLE_ADMIN"}));
            operations.insert(
                    new User(
                            null,
                            "phil", "webb",
                            new String[]{"ROLE_USER"}));
            operations.findAll(User.class).forEach(user -> {
                System.out.println("Loaded " + user);
            });
        };
    }
}
