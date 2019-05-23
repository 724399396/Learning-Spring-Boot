package com.greglturnquist.learningspringboot;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnClass(WebDriver.class)
@EnableConfigurationProperties(WebDriverConfigurationProperties.class)
@Import({ChromeDriverFactory.class,
        FirefoxDriverFactory.class, SafariDriverFactory.class})
public class WebDriverAutoConfiguration {

    @Primary
    @Bean(destroyMethod = "quit")
    @ConditionalOnMissingBean(WebDriver.class)
    public WebDriver webDriver(
            FirefoxDriverFactory firefoxDriverFactory,
            SafariDriverFactory safariDriverFactory,
            ChromeDriverFactory chromeDriverFactory) {
        WebDriver driver = firefoxDriverFactory.getObject();
        if (driver == null) {
            driver = safariDriverFactory.getObject();
        }
        if (driver == null) {
            driver = chromeDriverFactory.getObject();
        }
        return driver;
    }
}
