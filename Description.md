# SIS 2 — Test Automation with Playwright Test and CI

## Project Overview
Automated end-to-end test suite for [Practice Software Testing](https://practicesoftwaretesting.com/) — a demo e-commerce Toolshop application built for QA practice.

**Framework:** Playwright Test 1.65 (TypeScript)
**Browser:** Chromium (headless)
**Architecture:** Page Object Model with Playwright fixtures
**CI:** GitHub Actions, 4 parallel shards with a merged report

The suite was first submitted as a Spring Boot + Playwright Java project. It was ported to
Playwright Test for the CI assignment, because `forbidOnly`, the blob and JUnit reporters,
`--shard` and `merge-reports` are features of the Node test runner. The test cases are unchanged.

## Test Cases Automated

| # | Test Case | Spec | What it verifies |
|---|-----------|------|-----------------|
| 1 | User Registration | `register.spec.ts` | New user registers with unique data, redirected to login |
| 2 | Login (valid) | `login.spec.ts` | Customer logs in, redirected to account page |
| 3 | Login (invalid) | `login.spec.ts` | Invalid credentials show error message |
| 4 | Buy Product (full flow) | `buy-product.spec.ts` | Login -> browse -> add to cart -> billing -> payment -> success |
| 5 | Products displayed | `product-browsing.spec.ts` | Home page shows products |
| 6 | Sort products A-Z | `product-browsing.spec.ts` | Sorting returns correctly ordered results |
| 7 | Search products | `product-browsing.spec.ts` | Search returns relevant results |
| 8 | Contact form | `contact.spec.ts` | Message submission shows success alert |

## Project Structure

```
.github/workflows/
  playwright.yml                  # CI pipeline: 4 shards + report merge
tests/
  fixtures.ts                     # test/expect with the `customer` fixture
  data/
    user-factory.ts               # Test data factory (unique per call)
  pages/
    login-page.ts                 # Page Objects
    register-page.ts
    product-page.ts
    cart-page.ts
    checkout-page.ts
  login.spec.ts                   # Test specs
  register.spec.ts
  buy-product.spec.ts
  product-browsing.spec.ts
  contact.spec.ts
playwright.config.ts              # Local and CI configuration
merge.config.ts                   # Report merge configuration
package.json / package-lock.json
```

## Key Design Decisions

1. **Page Object Model** — Each screen has a page class. Specs don't contain locators.
2. **`data-test` attributes** — `testIdAttribute: 'data-test'` lets tests use `getByTestId`.
3. **Unique test data** — Registration uses timestamp-plus-random emails and generated passwords.
4. **No hardcoded waits** — Web-first assertions retry on their own; list checks use `expect.toPass`.
5. **Fresh account per test** — The `customer` fixture registers a user through the REST API, so
   there are no stored credentials and no shared account for parallel runs to lock.
6. **One switch for CI** — Everything CI-specific in the config depends on the `CI` variable.

## How to Run

```bash
npm ci
npx playwright install --with-deps chromium
npx playwright test
```

Requires: Node.js 20+. See the CI section of `README.md` for the pipeline.
