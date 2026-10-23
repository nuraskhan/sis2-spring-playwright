package com.kbtu.sis2.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class LoginPage {
    private final Page page;
    private final String baseUrl;

    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator submitBtn;
    private final Locator errorMessage;

    public LoginPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
        this.emailInput = page.locator("[data-test='email']");
        this.passwordInput = page.locator("[data-test='password']");
        this.submitBtn = page.locator("[data-test='login-submit']");
        this.errorMessage = page.locator("[data-test='login-error']");
    }

    public void open() {
        page.navigate(baseUrl + "/auth/login");
    }

    public void signIn(String email, String password) {
        emailInput.fill(email);
        passwordInput.fill(password);
        submitBtn.click();
    }

    public Locator getErrorMessage() {
        return errorMessage;
    }
}
