package com.greglturnquist.learningspringboot;

import org.apache.commons.lang3.ClassUtils;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

public class WebDriverAutoConfigurationTests {
    private AnnotationConfigApplicationContext context;
    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    private void load(Class<?>[] configs, String... environment) {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext();
        applicationContext
                .register(WebDriverAutoConfiguration.class);
        if (configs.length > 0) {
            applicationContext.register(configs);
        }
        TestPropertyValues.of(environment)
                .applyTo(applicationContext);
        applicationContext.refresh();
        this.context = applicationContext;
    }

    @Test
    public void fallbackToNonGuiModeWhenAllBrowsersDisabled() {
        load(new Class[]{},
                "com.greglturnquist.webdriver.firefox.enabled:false",
                "com.greglturnquist.webdriver.safari.enabled:false",
                "com.greglturnquist.webdriver.chrome.enabled:false");

        WebDriver driver = context.getBean(WebDriver.class);
        assertThat(ClassUtils.isAssignable(TakesScreenshot.class,
                driver.getClass())).isFalse();
        assertThat(ClassUtils.isAssignable(SafariDriver.class,
                driver.getClass())).isFalse();
    }

    @Test
    public void testWithMockedFirefox() {
        load(new Class[]{MockFirefoxConfiguration.class},
                "com.greglturnquist.webdriver.safari.enabled:false",
                "com.greglturnquist.webdriver.chrome.enabled:false");
        WebDriver driver = context.getBean(WebDriver.class);
        assertThat(ClassUtils.isAssignable(TakesScreenshot.class,
                driver.getClass())).isTrue();
        assertThat(ClassUtils.isAssignable(FirefoxDriver.class,
                driver.getClass())).isTrue();
    }
}
