# AI usage — CI pipeline assignment

This file covers the CI pipeline work (November 2026). The earlier Java version of the suite is
documented in `Prompt.md` and `Review.md`.

## Tool and model

- **Tool:** Claude Code (Anthropic's command-line coding agent), run in a terminal session on
  Windows 11 with access to the cloned repository.
- **Model:** Fable 5.2
- **Date:** 2026-11-22
- **Mode of work:** the agent read the repository and the assignment guide, proposed a plan for
  each part, and made the changes after I approved them. Every step is a separate git commit.
  The agent was told not to push; commits stay local until I push them.

## Prompts

The prompts are quoted as I typed them, in order.

**1. Setup**

> Now, we created session for now clone https://github.com/nuraskhan/sis2-spring-playwright here

> Now check my github token is it able to push/pull?

**2. Analysis**

> Each step will be commited without co-author tag, but dont push
> Now first task: just read and analyze and them write Claude.md (if exist re-check and moderate):

followed by the full text of the assignment guide ("SIS 2: CI pipeline for Playwright
step-by-step guide").

**3. Step 1 of the guide**

> No, everything is fine, lets star with section-1, analyze and provide how to do, before you act

> Ok, remove java if its not suit.
> Creds i guess already mock ones(especially for tests and open sourced)

**4. Remaining steps**

> Lets create other parts

## What the AI produced

| Area | Output |
| --- | --- |
| Analysis | `CLAUDE.md`: repository state, the six requirements, risks, decisions |
| Port | `package.json`, `tsconfig.json`, `tests/` (five page objects, user factory, fixtures, five specs) rewritten from the Java suite |
| Step 1 | `playwright.config.ts` with the CI branch, `merge.config.ts` |
| Step 2 | Local verification runs (listed below) |
| Step 3 | `.github/workflows/playwright.yml` |
| Step 5 | CI section, badge and timing table in `README.md`; `Description.md` updated |

## What the AI found and I decided

1. **The repository was Java, the assignment assumes TypeScript.** The agent pointed out that
   `forbidOnly`, the blob and JUnit reporters, `--shard` and `merge-reports` exist only in the
   Node Playwright Test runner, and that the checklist names `playwright.config.ts`. It offered
   two options: port to TypeScript, or keep Java with partial equivalents. I chose the port and
   told it to remove the Java code.
2. **Credentials.** The agent flagged the test account's email and password in the repository.
   I answered that they are mock data for a public demo site. When it then checked the account,
   the login returned 401: the site resets its database, so the account no longer existed. The
   agent replaced the stored account with a fixture that registers a new user through the site's
   API for each test.
3. **Sharding a small suite.** The agent warned before any measurement that with 8 tests and
   about a minute of setup per shard, four shards may not reduce the total time.

## What had to be corrected

| Problem | Cause | Fix |
| --- | --- | --- |
| `tsc` failed with `TS5108: Option 'moduleResolution=node10' has been removed` | The agent wrote a `tsconfig.json` in the style of TypeScript 5, but the installed version was 7.0.2 | Switched to `module: preserve` with `moduleResolution: bundler` |
| Stored test account unusable | Site database reset; account deleted | `customer` fixture registers a user per test |
| Sort and search assertions could read the list before it re-rendered | The Java version read the product list once after `waitForLoadState` | Retrying assertions (`expect.toPass`, locator filter with `toBeVisible`) |
| One long shell command that created all files failed with a quoting error | A password literal containing `$` and `!` inside a shell heredoc | Files were written one by one instead; nothing had been created by the failed command |

## How the result was verified

All checks were run locally by the agent and the output was shown to me.

| Check | Result |
| --- | --- |
| `npx tsc --noEmit` | no errors |
| `npx playwright test --list` | 8 tests in 5 files |
| `npx playwright test` | 8 passed |
| `CI=true npx playwright test` | 8 passed; `blob-report/report.zip` and `reports/junit.xml` created |
| `test.only` added temporarily, `CI=true` run | run failed on the forbidden `.only` (exit code 1); change reverted |
| Shards `1/2` and `2/2`, then `merge-reports` | 4 + 4 passed; merged `playwright-report/index.html` and `reports/junit-merged.xml` with 8 test cases |

Not verified locally: the workflow file itself. Docker and `act` are not installed on my
machine, so the YAML is exercised only on GitHub.

## Limits of the AI's contribution

- The timing numbers in the README must come from real GitHub Actions runs. Until those runs
  exist the table says "pending"; the agent did not invent figures.
- The agent cannot see the GitHub Actions UI. Run links, the cache-restore log line and the
  artefact list have to be checked by me on GitHub.
