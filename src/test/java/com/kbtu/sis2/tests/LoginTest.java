package com.kbtu.sis2.tests;

import com.kbtu.sis2.config.BaseTest;
import com.kbtu.sis2.pages.LoginPage;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class LoginTest extends BaseTest {

    @Value("${app.customer-email}")
    private String customerEmail;

    @Value("${app.customer-password}")
    private String customerPassword;

    @Test
    @DisplayName("Valid credentials redirect to account page")
    void validCredentialsRedirectToAccount() {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.open();
        loginPage.signIn(customerEmail, customerPassword);

        assertThat(page).hasURL(Pattern.compile(".*/account.*"),
                new PageAssertions.HasURLOptions().setTimeout(10000));
    }

    @Test
    @DisplayName("Invalid credentials show error message")
    void invalidCredentialsShowError() {
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.open();
        loginPage.signIn("invalid@test.com", "wrongpassword");

        assertThat(loginPage.getErrorMessage()).isVisible();
    }
}
