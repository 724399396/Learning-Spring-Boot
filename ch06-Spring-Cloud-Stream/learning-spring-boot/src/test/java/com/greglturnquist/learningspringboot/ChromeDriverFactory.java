package com.greglturnquist.learningspringboot;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;

public class ChromeDriverFactory implements ObjectFactory<ChromeDriver> {
    private WebDriverConfigurationProperties properties;

    public ChromeDriverFactory(WebDriverConfigurationProperties properties) {
        this.properties = properties;
    }

    @Override
    public ChromeDriver getObject() throws BeansException {
        if (properties.getChrome().isEnabled()) {
            try {
                return new ChromeDriver();
            } catch (WebDriverException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
