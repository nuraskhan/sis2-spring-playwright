# CLAUDE.md

Guidance for Claude Code when working in this repository.

## What this repo is

University assignment repo (KBTU, software testing course). It holds the **SIS 2** end-to-end suite
for the public demo shop <https://practicesoftwaretesting.com> and is now being extended with a
**CI pipeline for Playwright** (current assignment, deadline **2026-11-22 23:59**).

Remote: `github.com/nuraskhan/sis2-spring-playwright` (public, default branch `main`).

## Working rules

- **Commit after every step**, one logical change per commit.
- **No `Co-Authored-By` trailer** and no other AI attribution lines in commit messages.
- **Never push** unless the user explicitly asks. The same goes for opening PRs.
- No model identifiers in commit messages or code comments. The AI disclosure file is the only
  place the model is named, because the assignment requires it.
- Do not add secrets to the repo. Credentials come from environment variables / GitHub Actions
  secrets (see "Known problems").
- Every AI-assisted step must be recorded in the AI disclosure markdown (model, prompts, what was
  corrected). `Prompt.md` and `Review.md` currently describe only the earlier SIS 2 work.

## Current state of the code

Java project, **not** Node. There is no `package.json`, no `playwright.config.ts`, no `.github/`.

| Area | Detail |
| --- | --- |
| Build | Maven, `pom.xml`, Spring Boot parent 3.3.4, `java.version` 17 |
| Test stack | Playwright **Java** 1.49.0, JUnit 5, `@SpringBootTest` with profile `test` |
| Run | `mvn test` (Surefire: `**/*Test.java`, `forkCount=1`, `reuseForks=true`, alphabetical order) |
| Config | `src/test/resources/application-test.properties`: `app.base-url`, `app.headless`, `app.customer-email`, `app.customer-password` |
| Browser | Chromium, headless |

Layout under `src/test/java/com/kbtu/sis2/`:

- `config/PlaywrightConfig.java`: Spring beans for `Playwright` and `Browser` (singletons).
  The prototype `BrowserContext` / `Page` beans, the `baseUrl` bean and the
  `app.test-id-attribute` property are unused.
- `config/BaseTest.java`: base class; creates a fresh `BrowserContext` + `Page` per test in
  `@BeforeEach`, closes the context in `@AfterEach`; `testId(id)` helper for `[data-test='…']`.
- `data/UserFactory.java`: timestamp-based unique user (`record UserData`).
- `pages/`: `LoginPage`, `RegisterPage`, `ProductPage`, `CartPage`, `CheckoutPage`. Page objects
  are plain classes built with `new XPage(page, baseUrl)`, not Spring beans.
- `tests/`: 5 classes, 8 tests.

| Class | Tests |
| --- | --- |
| `RegisterTest` | register new user, redirected to `/auth/login` |
| `LoginTest` | valid login goes to `/account`; invalid login shows `login-error` |
| `BuyProductTest` | login, add first product, 4-step checkout, bank transfer, "Payment was successful" |
| `ProductBrowsingTest` | products shown; sort A-Z; search "Hammer" |
| `ContactTest` | contact form shows `.alert-success` |

`src/main` contains only `Sis2Application` with `spring.main.web-application-type=none`. There is
no application under test in this repo, so no `webServer` is needed; the target is the external
site.

## The CI assignment

Deliverables: `.github/workflows/playwright.yml`, a CI branch in `playwright.config.ts` plus a
small `merge.config.ts`, and a CI section in `README.md`.

| # | Requirement | Concrete form |
| --- | --- | --- |
| 1 | Runs on every push and PR | `on: push` and `on: pull_request`, no branch filter (+ `workflow_dispatch`) |
| 2 | Cached deps, browsers, full suite | `setup-node` with `cache: npm`, `npm ci`, `npx playwright install --with-deps chromium`, `npx playwright test` with no `--grep`/`--project` |
| 3 | HTML report + traces as artefacts, ≥ 7 days | `upload-artifact` with `retention-days: 14`; `trace: 'on-first-retry'` |
| 4 | CI-specific config | when `process.env.CI`: `forbidOnly: true`, `retries: 2`, reporters `list` + `blob` + `junit` |
| 5 | Parallelism + before/after runtime | 4-shard matrix + `merge-reports` job; timing table in README with links to both runs |
| 6 | Status badge for `main` | `actions/workflows/playwright.yml/badge.svg?branch=main` in README |

Expected artefacts: `playwright-report`, `test-results-1…4`, `junit-report` (blob reports are
intermediate, 1 day). `.gitignore` must list `node_modules/`, `test-results/`,
`playwright-report/`, `blob-report/`, `reports/`, `all-blob-reports/`, `.act-artifacts/`,
`.secrets`.

Evidence that only GitHub can provide, and therefore needs pushes the user must authorise:
a push run on a feature branch, a PR run, a second run whose `setup-node` log shows the npm cache
restored, a 1-shard run and a 4-shard run for the timing table (median of 2-3
`workflow_dispatch` runs each), and a run on `main` for the badge.

## Open decision: the suite is Java, the assignment assumes TypeScript

`forbidOnly`, the `blob`/`junit` reporters, `--shard` and `merge-reports` are features of the
Node **Playwright Test** runner. They do not exist in Playwright Java + JUnit 5, and the
submission checklist names `playwright.config.ts` explicitly. Two ways forward:

1. **Port the 8 tests to Playwright Test (TypeScript)** and build the pipeline exactly as the
   guide describes. Every checklist item is then literally verifiable. `Prompt.md` records that
   the suite was originally written in TypeScript with the same page objects, so the port is
   small. Open sub-question: keep the Java suite alongside or remove it.
2. **Keep Java** and approximate: Surefire `rerunFailingTestsCount`, Surefire JUnit XML, a matrix
   over test classes, manual `context.tracing()`. There is no equivalent of `forbidOnly`, no
   merged HTML report, and the npm-cache and `playwright.config.ts` checklist items cannot be met.

Recommendation: option 1. Confirm with the user before writing code.

## Known problems to handle during the work

- **Hardcoded credentials.** `application-test.properties` and `Prompt.md` contain the test
  account's email and password, and both are already in public git history. The assignment
  forbids hardcoded logins. Read them from environment variables (GitHub: Settings → Secrets and
  variables → Actions); `BASE_URL` goes in Actions *Variables*.
- **Account lockout under parallelism.** The site locks an account after repeated failed or
  concurrent logins; `customer@` and `customer2@` were both locked earlier, and parallel runs in
  the original TypeScript version caused locks (it was pinned to 1 worker). Two tests log in with
  the same shared account, so 4 shards × `workers: 2` × `retries: 2` can trip the lock. Options:
  register a fresh user per test through the API, or keep the login tests serial.
- **Shared public site.** Other students hit it at the same time, so failures can be external.
  Do not weaken assertions to hide them; retries and traces exist for this.
- **Small suite.** With 8 tests, each shard pays about a minute of setup, so 4 shards may not be
  faster in total. The README must report the real numbers and explain them.

## Facts about the site that tests rely on

- Locators use the `data-test` attribute. Payment fields have none; use placeholders
  (`Bank Name`, `Account Name`, `Account Number`).
- Checkout has 4 steps: cart → sign in → billing address → payment (`proceed-1`, `proceed-2`,
  `proceed-3`, `finish`).
- Registration rejects passwords found in breach lists (`Welcome01!` fails); generate them.
- Registration fields include `postal_code`, `house_number`, `street`, `city`, `state`, `phone`,
  `dob`, `country`.
- Contact message must be at least 50 characters.
- Search is not exact: "Hammer" also returns pliers. Assert that at least one result matches.
- "Payment was successful" is a `div` alert, not a heading.

## Local environment (checked 2026-11-22, Windows 11)

- Node 24.16.0 and npm 11.13.0 are installed. The assignment asks for Node 20 or 22 LTS; CI
  should pin 22.
- Java on `PATH` is 1.8 and Maven is not installed, so **`mvn test` cannot run on this machine**
  as it stands (the project needs JDK 17+).
- Docker and `act` are not available, so the workflow YAML can only be exercised on GitHub.
- Shell is PowerShell: `$env:CI = "true"; npx playwright test; Remove-Item Env:CI`. Git Bash is
  also available: `CI=true npx playwright test`.
