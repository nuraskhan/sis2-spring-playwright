package com.kbtu.sis2.tests;

import com.kbtu.sis2.config.BaseTest;
import com.microsoft.playwright.assertions.LocatorAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class ContactTest extends BaseTest {

    @Test
    @DisplayName("User can submit a contact message successfully")
    void submitContactMessage() {
        page.navigate(baseUrl + "/contact");

        testId("first-name").fill("Test");
        testId("last-name").fill("User");
        testId("email").fill("testuser@test.com");
        testId("subject").selectOption("webmaster");
        testId("message").fill(
                "This is a detailed test message for the contact form that exceeds the minimum fifty character requirement for validation."
        );
        testId("contact-submit").click();

        assertThat(page.locator(".alert-success")).isVisible(
                new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
    }
}
