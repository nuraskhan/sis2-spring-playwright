# Prompts Used for AI Assistance

> This file describes the original SIS 2 work (October 2026), when the suite was written in Java.
> The Java code has since been replaced by a TypeScript suite; see `AI_USAGE.md` for the CI
> pipeline assignment.

## AI Tool
**Claude Opus 4.6 (1M context)** — used through Claude Code CLI (terminal-based tool by Anthropic)

---

## Prompt 1 — Setting up the project

I already had the Week 6 lecture slides loaded in the conversation (the AI read all .pptx files from our course folder earlier for midterm prep). So it knew the 5 test cases from the slides, the project structure, the page object pattern, etc.

My prompt was basically:

> now, lets discuss how to implement Week 6 Materials and SIS 2 link
> DEADLINE: Oct 23, 23:59:59
> https://practicesoftwaretesting.com/
> At least 5 AT suits mentioned in the Pres - use any language you want with the Playwright framework.
> Description.md, Prompt.md, Review.md, Screenshots, Link to public GitHub repository.

The AI then created a full TypeScript + Playwright project: config, 5 page objects (LoginPage, RegisterPage, ProductPage, CartPage, CheckoutPage), a fixture file, data factory, and 5 test spec files with 8 tests total.

---

## Prompt 2 — Debugging (this took a while)

First run: 6 out of 8 tests failed. I just let the AI see the errors and screenshots, and said things like "fix it" basically. The AI would look at the screenshot, figure out what's wrong, and fix. This went back and forth about 7 times:

1. **Wrong field names** — AI guessed that register page has an `address` field. Screenshot showed it actually has `postal_code`, `house_number`, `street`, `phone` etc. AI ran a script to scrape all `data-test` attributes from every page of the real site, then rewrote the page objects.

2. **customer@ account locked** — The seeded account `customer@practicesoftwaretesting.com` was locked ("too many failed attempts"). AI switched to `customer2@` which worked.

3. **Contact form: "Message must be minimal 50 characters"** — My test message was too short. AI made it longer.

4. **Checkout had 4 steps not 2** — AI assumed simple checkout. Screenshot showed: Cart → Sign In → Billing Address → Payment. Had to add billing address filling (country dropdown, postal code, house number).

5. **Payment fields had no data-test attributes** — AI used `getByTestId('account-name')` but those didn't exist. Screenshot showed the fields only had placeholder text. Switched to `getByPlaceholder('Bank Name')` etc.

6. **"Payment was successful" was a div not a heading** — AI used `getByRole('heading')`, didn't find it. Screenshot showed it's a green alert box. Changed to `getByText()`.

7. **Password "Welcome01!" rejected as data leak** — The site checks passwords against breach databases. Changed to generated passwords with timestamps.

8. **Parallel tests caused account locks** — Set workers to 1 and added retry.

After all that, 8/8 tests passed in TypeScript.

---

## Prompt 3 — Rewrite to Spring Boot

Then I asked:

> Is it possible to do this on Spring?

AI said Spring is overkill for E2E tests but possible. I said:

> Delete the repo you created and recreate on Spring

AI rewrote everything to Java:
- `pom.xml` with Spring Boot 3.3 + Playwright Java 1.49 dependency
- `PlaywrightConfig.java` — Spring `@Configuration` with `@Bean` for Playwright, Browser, BrowserContext, Page
- `BaseTest.java` — abstract class with `@SpringBootTest`, `@BeforeEach`/`@AfterEach` for browser context lifecycle
- All page objects rewritten to Java classes
- `UserFactory.java` with Java `record` for test data
- `application-test.properties` for base URL and credentials
- 5 test classes with JUnit 5 `@Test` and `@DisplayName`

---

## Prompt 4 — Fixing Java-specific issues

3 more rounds of fixes for the Java version:

1. **Wrong import for assertion options** — AI used `com.microsoft.playwright.Locator.IsVisibleOptions` but the correct one for assertions is `com.microsoft.playwright.assertions.LocatorAssertions.IsVisibleOptions`. Same thing for `PageAssertions.HasURLOptions`. Java has both classes with the same name in different packages.

2. **customer2@ also got locked** — Too many test runs. AI registered a fresh account via the REST API: `aldongar.kbtu@test.com` / `KbtuTest2026!`

3. **Search returned unrelated products** — Searching "Hammer" also showed "Combination Pliers". Changed assertion from "all results must contain hammer" to "at least one result must contain hammer".

After that: `mvn test` → BUILD SUCCESS, 8 tests, 0 failures.

---

## Prompt 5 — Credentials cleanup

I pointed out the auto-generated credentials looked weird:

> what a credentials

AI registered a cleaner account (`aldongar.kbtu@test.com` / `KbtuTest2026!`) and updated the properties file.

---

## Tools and Versions
- **AI Tool:** Claude Opus 4.6 (1M context) via Claude Code CLI
- **Java:** OpenJDK 25.0.1
- **Spring Boot:** 3.3.4
- **Playwright Java:** 1.49.0
- **Maven:** 3.9.9
- **OS:** macOS (Darwin 27.0.0)
