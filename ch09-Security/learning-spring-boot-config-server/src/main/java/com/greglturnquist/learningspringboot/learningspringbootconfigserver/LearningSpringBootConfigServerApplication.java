package com.greglturnquist.learningspringboot.learningspringbootconfigserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class LearningSpringBootConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningSpringBootConfigServerApplication.class, args);
	}

}
