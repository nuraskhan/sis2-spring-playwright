# CLAUDE.md

Guidance for Claude Code when working in this repository.

## What this repo is

University assignment repo (KBTU, software testing course). It holds the **SIS 2** end-to-end suite
for the public demo shop <https://practicesoftwaretesting.com> and is being extended with a
**CI pipeline for Playwright** (current assignment, deadline **2026-11-22 23:59**).

Remote: `github.com/nuraskhan/sis2-spring-playwright` (public, default branch `main`). The repo
name still says "spring"; the Java/Spring suite was removed on 2026-11-22.

## Working rules

- **Commit after every step**, one logical change per commit.
- **No `Co-Authored-By` trailer** and no other AI attribution lines in commit messages.
- **Never push** unless the user explicitly asks. The same goes for opening PRs.
- No model identifiers in commit messages or code comments. The AI disclosure file is the only
  place the model is named, because the assignment requires it.
- Do not add secrets to the repo.
- Every AI-assisted step must be recorded in the AI disclosure markdown (model, prompts, what was
  corrected). `Prompt.md` and `Review.md` currently describe only the earlier SIS 2 work.
- Analyse and present a plan before acting on each section of the assignment guide; wait for the
  user's go-ahead.

## Current state of the code

Node project using **Playwright Test** (TypeScript).

| Area | Detail |
| --- | --- |
| Runner | `@playwright/test` 1.65.1 (pinned exactly), TypeScript 7.0.2, `@types/node` 22 |
| Run | `npx playwright test` (`npm test`); CI mode `npm run test:ci`; type-check `npm run typecheck` |
| Config | `playwright.config.ts`, `merge.config.ts`, `tsconfig.json` |
| Browser | Chromium only |
| Target | external site; `BASE_URL` (default `https://practicesoftwaretesting.com`), `API_URL` (default `https://api.practicesoftwaretesting.com`) |

Layout:

- `tests/fixtures.ts`: exports `test` and `expect`. Adds the `customer` fixture, which registers
  a fresh user through `POST {API_URL}/users/register` before the test and yields its
  credentials. **Import `test`/`expect` from here, not from `@playwright/test`.**
- `tests/data/user-factory.ts`: `generateUser()`, unique per call (timestamp + random salt).
- `tests/pages/`: `LoginPage`, `RegisterPage`, `ProductPage`, `CartPage`, `CheckoutPage`.
  Plain classes taking `page`; locators use `getByTestId` (`testIdAttribute: 'data-test'`).
- Five spec files, 8 tests:

| Spec | Tests |
| --- | --- |
| `register.spec.ts` | register new user through the UI, redirected to `/auth/login` |
| `login.spec.ts` | valid login goes to `/account`; invalid login shows `login-error` |
| `buy-product.spec.ts` | login, add first product, 4-step checkout, bank transfer, "Payment was successful" |
| `product-browsing.spec.ts` | products shown; sort A-Z; search "Hammer" |
| `contact.spec.ts` | contact form shows `.alert-success` |

`playwright.config.ts` when `CI` is set: `forbidOnly: true`, `retries: 2`, `workers: 2`, reporters
`list` + `blob` + `junit` (`reports/junit.xml`). Otherwise `list` + `html`. Always:
`fullyParallel`, `trace: 'on-first-retry'`, screenshot on failure, video retained on failure.
No `webServer`, because the app under test is not in this repo.

`merge.config.ts` is used only by `npx playwright merge-reports --config merge.config.ts <dir>`
and writes `playwright-report/` plus `reports/junit-merged.xml`.

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
intermediate, 1 day).

Evidence that only GitHub can provide, and therefore needs pushes the user must authorise:
a push run on a feature branch, a PR run, a second run whose `setup-node` log shows the npm cache
restored, a 1-shard run and a 4-shard run for the timing table (median of 2-3
`workflow_dispatch` runs each), and a run on `main` for the badge.

### Progress against the guide

| Guide step | Status |
| --- | --- |
| 1. CI configuration (`playwright.config.ts`, `merge.config.ts`) | done; suite ported from Java first |
| 2. Run in CI mode locally (`forbidOnly` check, local shards + merge) | plain and `CI=true` runs pass 8/8; `test.only` check and shard merge not done yet |
| 3. GitHub Actions workflow | not started |
| 4. Docker / `act` | not possible on this machine (neither is installed) |
| 5. Timing table, badge, README CI section | not started |
| AI disclosure file for this assignment | not started |

`README.md` and `Description.md` still describe the removed Java suite and must be rewritten.

## Decisions already made

- **TypeScript instead of Java (user, 2026-11-22).** `forbidOnly`, the `blob`/`junit` reporters,
  `--shard` and `merge-reports` exist only in the Node Playwright Test runner, and the checklist
  names `playwright.config.ts`. The user chose to remove the Java suite rather than keep both.
- **Per-test customer registered through the API.** The site's database is reset periodically,
  so the account that used to be in `application-test.properties` no longer exists (login
  returned 401 on 2026-11-22). A stored account cannot work long-term; registering one per test
  also means no credentials in the repo, no GitHub secret, and no shared account for parallel
  shards to lock.
- **Old credentials left in `Prompt.md`.** The user considers them mock data for a public demo
  site; the account is gone anyway. Do not scrub them unless asked.

## Things to keep in mind

- **Shared public site.** Other students hit it at the same time and it resets its data, so
  failures can be external. Do not weaken assertions to hide them; retries and traces exist for
  this.
- **Lockout.** The site locks an account after repeated failed logins. The invalid-login test
  uses a non-existent address on purpose; never point it at a real account.
- **Small suite.** With 8 tests (about 11 s locally), each shard pays about a minute of setup,
  so 4 shards will probably not be faster in total. The README must report the real numbers and
  explain them.

## Facts about the site that tests rely on

- Elements are marked with `data-test`. Payment fields have none; use placeholders
  (`Bank Name`, `Account Name`, `Account Number`).
- Checkout has 4 steps: cart → sign in → billing address → payment (`proceed-1`, `proceed-2`,
  `proceed-3`, `finish`).
- Registration rejects passwords found in breach lists (`Welcome01!` fails); generate them.
- Register API body: `first_name`, `last_name`, `dob`, `phone`, `email`, `password`, and a nested
  `address` object (`street`, `house_number`, `city`, `state`, `country`, `postal_code`).
  Returns 201.
- Contact message must be at least 50 characters.
- Search is not exact: "Hammer" also returns pliers. Assert that a matching result is present.
- Sorting and searching re-render the list asynchronously; use retrying assertions rather than
  reading the list once.
- "Payment was successful" is a `div` alert, not a heading.

## Local environment (checked 2026-11-22, Windows 11)

- Node 24.16.0 and npm 11.13.0. The assignment asks for Node 20 or 22 LTS; CI should pin 22.
- Chromium for Playwright 1.65.1 is installed in the user's `ms-playwright` cache.
- Docker and `act` are not available, so the workflow YAML can only be exercised on GitHub.
- TypeScript 7 removed `moduleResolution: node10`; `tsconfig.json` uses `module: preserve` with
  `moduleResolution: bundler`.
- The Bash tool is Git Bash: `CI=true npx playwright test`. In PowerShell:
  `$env:CI = "true"; npx playwright test; Remove-Item Env:CI`. `npm run test:ci` works in both.
