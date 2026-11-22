# Review of AI Results

> This review covers the original Java version of the suite (October 2026). For the CI pipeline
> assignment and the TypeScript port see `AI_USAGE.md`.

## What the AI Generated

Claude Opus 4.6 generated a complete Spring Boot + Playwright Java project:
- Spring config with `@Bean` lifecycle management for Playwright/Browser/Page
- Base test class with `@SpringBootTest` and automatic context/page setup/teardown
- 5 page objects, 5 test classes (8 tests total), data factory
- `pom.xml` with Spring Boot parent, Playwright dependency, surefire config

## What Was Wrong (and how it was fixed)

### 1. Wrong Playwright Java assertion API classes
**AI used:** `com.microsoft.playwright.Locator.IsVisibleOptions` and `com.microsoft.playwright.Page.HasURLOptions`
**Correct:** `com.microsoft.playwright.assertions.LocatorAssertions.IsVisibleOptions` and `com.microsoft.playwright.assertions.PageAssertions.HasURLOptions`
**Why:** AI mixed up the action options with assertion options — they have the same names but live in different packages. This is a Java-specific issue not present in the TypeScript API.

### 2. Shared account locked
**AI used:** `customer2@practicesoftwaretesting.com` (from course slides)
**Problem:** Account got locked from repeated test runs (rate limiting on shared public site)
**Fix:** Registered a fresh dedicated test account via the REST API. This is the correct approach — never depend on shared test data (Week 6: "other people are using it at the same time").

### 3. Search assertion too strict
**AI assertion:** Every result must contain the search term
**Reality:** The site returns related products too (e.g., "Pliers" for "Hammer" search)
**Fix:** Changed to "at least one result must match" — asserting the search functionality works without assuming exact-match behavior.

## What the AI Got Right

- **Spring Boot integration** — Proper `@Configuration` with `@Bean` lifecycle, `@Scope("prototype")` for pages, `@Value` injection for config
- **Page Object Model** — Clean separation, locators in page classes only
- **Test independence** — Each test gets a fresh `BrowserContext` and `Page` via `@BeforeEach`/`@AfterEach`
- **Assertions** — Used `PlaywrightAssertions.assertThat()` with auto-retry (not manual checks)
- **Locator strategy** — `data-test` attributes, `getByPlaceholder` for fields without test IDs
- **Data factory** — Timestamp-based unique emails and passwords using Java records

## Summary

The Spring Boot rewrite required 3 fixes (API class names, account lock, search assertion). The architecture decisions were sound — Spring DI for browser lifecycle, POM pattern, externalized config. Key lesson: Playwright's Java API has subtle differences from the TypeScript version, and AI training data leans toward the Node.js API.
