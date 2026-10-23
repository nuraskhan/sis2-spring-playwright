package com.kbtu.sis2.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class CartPage {
    private final Page page;

    public CartPage(Page page) {
        this.page = page;
    }

    public Locator getCartQuantity() {
        return page.locator("[data-test='cart-quantity']");
    }

    public void openCart() {
        getCartQuantity().click();
    }

    public Locator getCartItems() {
        return page.locator("[data-test='product-title']");
    }

    public void proceedToCheckout() {
        page.locator("[data-test='proceed-1']").click();
    }
}
