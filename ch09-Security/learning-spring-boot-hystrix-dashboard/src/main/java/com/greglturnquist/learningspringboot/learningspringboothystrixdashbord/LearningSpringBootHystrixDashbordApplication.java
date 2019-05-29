package com.greglturnquist.learningspringboot.learningspringboothystrixdashbord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrixDashboard
public class LearningSpringBootHystrixDashbordApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningSpringBootHystrixDashbordApplication.class, args);
	}

}
