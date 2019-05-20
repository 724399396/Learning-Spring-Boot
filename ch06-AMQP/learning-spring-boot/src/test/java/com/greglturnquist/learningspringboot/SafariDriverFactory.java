package com.greglturnquist.learningspringboot;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;

public class SafariDriverFactory implements ObjectFactory<SafariDriver> {
    private WebDriverConfigurationProperties properties;

    public SafariDriverFactory(WebDriverConfigurationProperties properties) {
        this.properties = properties;
    }

    @Override
    public SafariDriver getObject() throws BeansException {
        if (properties.getChrome().isEnabled()) {
            try {
                return new SafariDriver();
            } catch (WebDriverException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
