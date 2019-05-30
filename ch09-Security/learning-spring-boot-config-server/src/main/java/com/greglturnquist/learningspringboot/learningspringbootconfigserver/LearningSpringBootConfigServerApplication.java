package com.greglturnquist.learningspringboot.learningspringbootconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
@EnableConfigServer
public class LearningSpringBootConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningSpringBootConfigServerApplication.class, args);
    }

    @Bean
    UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("user")
                        .password("password")
                        .roles("USER").build()
        );
    }
}
