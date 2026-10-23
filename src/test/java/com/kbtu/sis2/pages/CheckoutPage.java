package com.kbtu.sis2.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.SelectOption;

public class CheckoutPage {
    private final Page page;

    public CheckoutPage(Page page) {
        this.page = page;
    }

    public Locator getProceedStep2() {
        return page.locator("[data-test='proceed-2']");
    }

    public Locator getProceedStep3() {
        return page.locator("[data-test='proceed-3']");
    }

    public Locator getFinishBtn() {
        return page.locator("[data-test='finish']");
    }

    public void fillBillingAddress() {
        page.locator("[data-test='country']").selectOption(new SelectOption().setLabel("Germany"));
        page.locator("[data-test='postal_code']").fill("10115");
        page.locator("[data-test='house_number']").fill("42");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public void fillBankTransferPayment() {
        page.locator("[data-test='payment-method']").selectOption(new SelectOption().setLabel("Bank Transfer"));
        page.getByPlaceholder("Bank Name").fill("Test Bank");
        page.getByPlaceholder("Account Name").fill("Test User");
        page.getByPlaceholder("Account Number").fill("1234567890");
    }

    public Locator getSuccessMessage() {
        return page.getByText("Payment was successful");
    }
}
