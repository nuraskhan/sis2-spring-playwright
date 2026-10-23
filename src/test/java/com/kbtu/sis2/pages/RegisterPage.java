package com.kbtu.sis2.pages;

import com.kbtu.sis2.data.UserFactory.UserData;
import com.microsoft.playwright.Page;

public class RegisterPage {
    private final Page page;
    private final String baseUrl;

    public RegisterPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void open() {
        page.navigate(baseUrl + "/auth/register");
    }

    public void register(UserData user) {
        page.locator("[data-test='first-name']").fill(user.firstName());
        page.locator("[data-test='last-name']").fill(user.lastName());
        page.locator("[data-test='dob']").fill(user.dob());
        page.locator("[data-test='country']").selectOption(user.country());
        page.locator("[data-test='postal_code']").fill(user.postcode());
        page.locator("[data-test='house_number']").fill(user.houseNumber());
        page.locator("[data-test='street']").fill(user.street());
        page.locator("[data-test='city']").fill(user.city());
        page.locator("[data-test='state']").fill(user.state());
        page.locator("[data-test='phone']").fill(user.phone());
        page.locator("[data-test='email']").fill(user.email());
        page.locator("[data-test='password']").fill(user.password());
        page.locator("[data-test='register-submit']").click();
    }
}
