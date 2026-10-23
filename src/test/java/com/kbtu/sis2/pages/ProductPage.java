package com.kbtu.sis2.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class ProductPage {
    private final Page page;
    private final String baseUrl;

    public ProductPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public void open() {
        page.navigate(baseUrl + "/");
    }

    public Locator getProductNames() {
        return page.locator("[data-test='product-name']");
    }

    public void clickFirstProduct() {
        getProductNames().first().click();
    }

    public void addToCart() {
        page.locator("[data-test='add-to-cart']").click();
    }

    public Locator getSearchInput() {
        return page.locator("[data-test='search-query']");
    }

    public void search(String query) {
        getSearchInput().fill(query);
        page.locator("[data-test='search-submit']").click();
        page.waitForLoadState();
    }

    public void sortByNameAsc() {
        page.locator("[data-test='sort']").selectOption("name,asc");
        page.waitForLoadState();
    }
}
