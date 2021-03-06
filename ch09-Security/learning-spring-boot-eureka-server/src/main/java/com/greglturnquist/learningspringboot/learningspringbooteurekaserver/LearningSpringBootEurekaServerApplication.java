package com.greglturnquist.learningspringboot.learningspringbooteurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@SpringBootApplication
@EnableEurekaServer
public class LearningSpringBootEurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningSpringBootEurekaServerApplication.class, args);

	}

	@Bean
	UserDetailsService userDetailsService() {
		return new InMemoryUserDetailsManager(
				User
						.withUsername("user")
						.password("password")
						.roles("USER").build());
	}
}
