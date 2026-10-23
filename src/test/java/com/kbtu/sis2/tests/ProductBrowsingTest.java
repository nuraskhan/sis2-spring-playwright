package com.kbtu.sis2.tests;

import com.kbtu.sis2.config.BaseTest;
import com.kbtu.sis2.pages.ProductPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductBrowsingTest extends BaseTest {

    @Test
    @DisplayName("Products are displayed on the home page")
    void productsAreDisplayed() {
        ProductPage productPage = new ProductPage(page, baseUrl);
        productPage.open();

        assertThat(productPage.getProductNames().first()).isVisible();
        assertTrue(productPage.getProductNames().count() > 0);
    }

    @Test
    @DisplayName("User can sort products by name A-Z")
    void sortProductsByNameAsc() {
        ProductPage productPage = new ProductPage(page, baseUrl);
        productPage.open();
        productPage.sortByNameAsc();

        assertThat(productPage.getProductNames().first()).isVisible();

        List<String> names = productPage.getProductNames().allTextContents()
                .stream().map(String::trim).toList();
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);
        assertEquals(sorted, names);
    }

    @Test
    @DisplayName("User can search for a product by name")
    void searchProductByName() {
        ProductPage productPage = new ProductPage(page, baseUrl);
        productPage.open();
        productPage.search("Hammer");

        assertThat(productPage.getProductNames().first()).isVisible();

        List<String> names = productPage.getProductNames().allTextContents();
        assertFalse(names.isEmpty(), "Search should return at least one result");
        // Verify at least one result actually matches the search term
        boolean anyMatch = names.stream()
                .anyMatch(n -> n.toLowerCase().contains("hammer"));
        assertTrue(anyMatch, "At least one product should contain 'hammer', got: " + names);
    }
}
