package com.kbtu.sis2.tests;

import com.kbtu.sis2.config.BaseTest;
import com.kbtu.sis2.data.UserFactory;
import com.kbtu.sis2.pages.RegisterPage;
import com.microsoft.playwright.assertions.PageAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class RegisterTest extends BaseTest {

    @Test
    @DisplayName("New user can register and is redirected to login")
    void newUserCanRegister() {
        UserFactory.UserData user = UserFactory.generateUser();
        RegisterPage registerPage = new RegisterPage(page, baseUrl);

        registerPage.open();
        registerPage.register(user);

        assertThat(page).hasURL(Pattern.compile(".*/auth/login.*"),
                new PageAssertions.HasURLOptions().setTimeout(15000));
    }
}
