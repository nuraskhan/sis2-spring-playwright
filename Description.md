# SIS 2 — Test Automation with Spring Boot + Playwright

## Project Overview
Automated end-to-end test suite for [Practice Software Testing](https://practicesoftwaretesting.com/) — a demo e-commerce Toolshop application built for QA practice.

**Framework:** Spring Boot 3.3 + Playwright Java + JUnit 5
**Browser:** Chromium (headless)
**Architecture:** Page Object Model with Spring DI

## Test Cases Automated

| # | Test Case | Class | What it verifies |
|---|-----------|-------|-----------------|
| 1 | User Registration | `RegisterTest` | New user registers with unique data, redirected to login |
| 2 | Login (valid) | `LoginTest` | Existing customer logs in, redirected to account page |
| 3 | Login (invalid) | `LoginTest` | Invalid credentials show error message |
| 4 | Buy Product (full flow) | `BuyProductTest` | Login -> browse -> add to cart -> billing -> payment -> success |
| 5 | Products displayed | `ProductBrowsingTest` | Home page shows products |
| 6 | Sort products A-Z | `ProductBrowsingTest` | Sorting returns correctly ordered results |
| 7 | Search products | `ProductBrowsingTest` | Search returns relevant results |
| 8 | Contact form | `ContactTest` | Message submission shows success alert |

## Project Structure

```
src/
  main/java/com/kbtu/sis2/
    Sis2Application.java              # Spring Boot entry point
  test/java/com/kbtu/sis2/
    config/
      PlaywrightConfig.java           # Spring beans: Playwright, Browser, Page
      BaseTest.java                   # Abstract base with @SpringBootTest
    data/
      UserFactory.java                # Test data factory (unique per run)
    pages/
      LoginPage.java                  # Page Object
      RegisterPage.java
      ProductPage.java
      CartPage.java
      CheckoutPage.java
    tests/
      LoginTest.java                  # Test specs
      RegisterTest.java
      BuyProductTest.java
      ProductBrowsingTest.java
      ContactTest.java
  test/resources/
    application-test.properties       # Config: baseURL, credentials
pom.xml
```

## Key Design Decisions

1. **Spring Boot for DI** — Playwright browser and page beans managed by Spring context. Config via `application-test.properties`.
2. **Page Object Model** — Each screen has a page class. Tests don't contain locators.
3. **`data-test` attributes** — Locators use `[data-test='...']` matching the app's markup.
4. **Unique test data** — Registration uses timestamp-based emails/passwords.
5. **No hardcoded waits** — Playwright assertions auto-retry with configurable timeouts.
6. **Fresh test account** — Registered via API to avoid shared account locks.

## How to Run

```bash
mvn test
```

Requires: Java 17+, Maven 3.8+. Playwright downloads Chromium automatically on first run.
