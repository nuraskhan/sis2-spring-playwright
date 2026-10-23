package com.kbtu.sis2.config;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseTest {

    @Autowired
    protected Browser browser;

    @Value("${app.base-url}")
    protected String baseUrl;

    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    void setUp() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void tearDown() {
        if (context != null) {
            context.close();
        }
    }

    protected Locator testId(String id) {
        return page.locator("[data-test='" + id + "']");
    }
}
