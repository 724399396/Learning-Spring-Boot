package com.greglturnquist.learningspringboot;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Configuration
public class MockFirefoxConfiguration {
    @Bean
    FirefoxDriverFactory firefoxDriverFactory() {
        FirefoxDriverFactory factory =
                mock(FirefoxDriverFactory.class);
        given(factory.getObject())
                .willReturn(mock(FirefoxDriver.class));
        return factory;
    }
}
