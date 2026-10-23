package com.kbtu.sis2.config;

import com.microsoft.playwright.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class PlaywrightConfig {

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.headless:true}")
    private boolean headless;

    @Bean(destroyMethod = "close")
    public Playwright playwright() {
        return Playwright.create();
    }

    @Bean(destroyMethod = "close")
    public Browser browser(Playwright playwright) {
        return playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless)
        );
    }

    @Bean
    @Scope("prototype")
    public BrowserContext browserContext(Browser browser) {
        return browser.newContext();
    }

    @Bean
    @Scope("prototype")
    public Page page(BrowserContext context) {
        return context.newPage();
    }

    @Bean
    public String baseUrl() {
        return baseUrl;
    }
}
