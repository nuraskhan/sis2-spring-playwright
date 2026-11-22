# SIS 2 — Playwright Test Automation with CI

[![Playwright Tests](https://github.com/nuraskhan/sis2-spring-playwright/actions/workflows/playwright.yml/badge.svg?branch=main)](https://github.com/nuraskhan/sis2-spring-playwright/actions/workflows/playwright.yml)

Automated end-to-end suite for the [Practice Software Testing Toolshop](https://practicesoftwaretesting.com/),
written with Playwright Test (TypeScript) and run on every push and pull request by GitHub Actions.

## Quick start

```bash
npm ci
npx playwright install --with-deps chromium
npx playwright test
npx playwright show-report
```

Requires Node.js 20 or newer (CI uses 22).

## Test cases

| Spec | Test | What it verifies |
| --- | --- | --- |
| `register.spec.ts` | Register | A new user registers with unique data and lands on the login page |
| `login.spec.ts` | Login (valid) | A customer signs in and is redirected to the account page |
| `login.spec.ts` | Login (invalid) | Wrong credentials show an error message |
| `buy-product.spec.ts` | Buy product | Login, add to cart, billing address, bank transfer, success message |
| `product-browsing.spec.ts` | Products displayed | The home page lists products |
| `product-browsing.spec.ts` | Sort A-Z | Sorting by name returns an ordered list |
| `product-browsing.spec.ts` | Search | Searching "Hammer" returns matching products |
| `contact.spec.ts` | Contact form | Submitting a message shows a success alert |

## Architecture

- **Page Object Model**: `tests/pages/`, one class per screen; specs contain no selectors.
- **Fixtures**: `tests/fixtures.ts` adds a `customer` fixture that registers a fresh account
  through the site's REST API before each test that needs to log in.
- **Test data**: `tests/data/user-factory.ts` generates a unique user per call.
- **No stored credentials**: the demo site resets its database, so a fixed account does not
  survive; creating one per test also keeps parallel shards from sharing an account.
- **Locators**: the site's `data-test` attribute, through `getByTestId`.

| Variable | Default | Purpose |
| --- | --- | --- |
| `BASE_URL` | `https://practicesoftwaretesting.com` | Site under test |
| `API_URL` | `https://api.practicesoftwaretesting.com` | API used to register the test customer |
| `CI` | unset | Switches the config to CI mode |

## CI

**CI system:** GitHub Actions. It is built into GitHub, where the repository already lives, needs
no server of its own, is free for public repositories and supports matrices for sharding.

**Pipeline** (`.github/workflows/playwright.yml`):

- runs on every push and every pull request, with no branch filter; `workflow_dispatch` allows
  manual runs;
- `npm ci` with the npm cache (`actions/setup-node`, `cache: npm`), Chromium installed with its
  system dependencies;
- the full Playwright suite, without `--grep` or `--project` filters, split into 4 shards
  (`--shard=i/4`) that run in parallel;
- the `merge-reports` job merges the shards' blob reports into one HTML report and one JUnit
  file; it runs even when tests failed;
- artefacts: `playwright-report` (HTML), `test-results-N` (traces, screenshots, videos, JUnit per
  shard), `junit-report` (merged JUnit); all kept for 14 days;
- a newer push to the same branch cancels the outdated run.

**CI settings** (`playwright.config.ts`, when `CI=true`): `forbidOnly: true`, `retries: 2`,
`workers: 2`, reporters `list`, `blob`, `junit`. Always on: `trace: 'on-first-retry'`,
screenshots on failure, video retained on failure.

To open a trace, download a `test-results-N` artefact and run
`npx playwright show-trace trace.zip`, or drop the file onto <https://trace.playwright.dev>.

### Runtime

The baseline is the same workflow started manually with the `shards` input set to `1`; the
sharded figure is the default 4-shard run.
| Configuration | Total run time | Slowest shard job | Run |
| --- | --- | --- | --- |
| 1 job, no sharding | BASE_TOTAL | BASE_JOB | [run](BASE_URL) |
| 4 shards + merge | 1m 19s | 35s (+ 22s merge) | [run #6](https://github.com/nuraskhan/sis2-spring-playwright/commit/359fa5706a565a326e1f6debe2f1b7d01f55feba/checks) |

Times are whole GitHub Actions jobs, including runner start, `npm ci` and browser install, not
only the test step.

Conclusion: with only 8 tests (about 11 s of actual test time), each job spends most of its time
on setup: starting the runner, restoring the npm cache and installing Chromium. Sharding splits
only the test time, while every shard repeats the setup and the merge job adds about 20 s more.
For a suite this small, 4 shards are therefore not faster than one job; sharding starts to pay
off only when the test time is much larger than the setup time.

### Running in CI mode locally

```bash
npm ci
npx playwright install --with-deps chromium
CI=true npx playwright test          # PowerShell: npm run test:ci
```

Two shards and a merged report, as the pipeline does it:

```bash
mkdir -p all-blob-reports
CI=true npx playwright test --shard=1/2 && cp blob-report/*.zip all-blob-reports/
CI=true npx playwright test --shard=2/2 && cp blob-report/*.zip all-blob-reports/
npx playwright merge-reports --config merge.config.ts ./all-blob-reports
npx playwright show-report
```

## AI disclosure

AI assistance was used. See `AI_USAGE.md` for the CI assignment (tool, model, prompts, what was
checked and corrected). `Prompt.md` and `Review.md` cover the earlier Java version of the suite.
