# SIS 2 — Spring Boot + Playwright Test Automation

Automated E2E test suite for [Practice Software Testing Toolshop](https://practicesoftwaretesting.com/).

## Quick Start

```bash
mvn test
```

Requires: Java 17+, Maven 3.8+

## 8 Test Cases

| Test | Description |
|------|-------------|
| Register | New user registration with unique data |
| Login (valid) | Existing customer redirected to account |
| Login (invalid) | Error message shown |
| Buy Product | Full purchase: browse -> cart -> checkout -> payment |
| Products displayed | Home page loads products |
| Sort A-Z | Products sorted correctly |
| Search | Search returns matching results |
| Contact form | Message submission succeeds |

## Architecture

- **Spring Boot 3.3** — DI for Playwright browser lifecycle
- **Playwright Java 1.49** — Browser automation
- **JUnit 5** — Test framework
- **Page Object Model** — `pages/` package, one class per screen
- **Config** — `application-test.properties` for baseURL and credentials

## AI Disclosure (Level 1)

See `Prompt.md` for prompts used and `Review.md` for corrections.
