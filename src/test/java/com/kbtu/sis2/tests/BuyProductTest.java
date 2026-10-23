package com.kbtu.sis2.tests;

import com.kbtu.sis2.config.BaseTest;
import com.kbtu.sis2.pages.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.assertions.PageAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

class BuyProductTest extends BaseTest {

    @Value("${app.customer-email}")
    private String customerEmail;

    @Value("${app.customer-password}")
    private String customerPassword;

    @Test
    @DisplayName("Logged-in user can add product to cart and complete purchase")
    void fullPurchaseFlow() {
        // Login
        LoginPage loginPage = new LoginPage(page, baseUrl);
        loginPage.open();
        loginPage.signIn(customerEmail, customerPassword);
        assertThat(page).hasURL(Pattern.compile(".*/account.*"),
                new PageAssertions.HasURLOptions().setTimeout(10000));

        // Browse and add product
        ProductPage productPage = new ProductPage(page, baseUrl);
        productPage.open();
        assertThat(productPage.getProductNames().first()).isVisible();
        productPage.clickFirstProduct();
        productPage.addToCart();

        // Verify cart updated
        CartPage cartPage = new CartPage(page);
        assertThat(cartPage.getCartQuantity()).hasText("1",
                new LocatorAssertions.HasTextOptions().setTimeout(10000));

        // Go to cart and checkout
        cartPage.openCart();
        assertThat(cartPage.getCartItems().first()).isVisible();
        cartPage.proceedToCheckout();

        // Checkout flow
        CheckoutPage checkoutPage = new CheckoutPage(page);

        // Step 2 - already signed in
        assertThat(checkoutPage.getProceedStep2()).isVisible(
                new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
        checkoutPage.getProceedStep2().click();

        // Step 3 - billing address
        checkoutPage.fillBillingAddress();
        assertThat(checkoutPage.getProceedStep3()).isEnabled(
                new LocatorAssertions.IsEnabledOptions().setTimeout(10000));
        checkoutPage.getProceedStep3().click();

        // Step 4 - payment
        checkoutPage.fillBankTransferPayment();
        checkoutPage.getFinishBtn().click();

        // Verify success
        assertThat(checkoutPage.getSuccessMessage()).isVisible(
                new LocatorAssertions.IsVisibleOptions().setTimeout(15000));
    }
}
