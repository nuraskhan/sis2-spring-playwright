import { defineConfig, devices } from '@playwright/test';

// GitHub Actions (and GitLab) set CI automatically. Everything CI-specific
// hangs off this one flag; without it the config behaves as a local run.
const isCI = !!process.env.CI;

export default defineConfig({
  testDir: './tests',
  fullyParallel: true,            // shards split individual tests, not whole files
  forbidOnly: isCI,               // a forgotten test.only fails the pipeline
  retries: isCI ? 2 : 0,          // an unstable test is reported as flaky, not red
  workers: isCI ? 2 : undefined,  // GitHub-hosted runners have 2-4 vCPUs
  reporter: isCI
    ? [
        ['list'],
        ['blob'],                                        // merged across shards later
        ['junit', { outputFile: 'reports/junit.xml' }],
      ]
    : [['list'], ['html', { open: 'on-failure' }]],
  use: {
    // The application under test is an external public site, so there is no
    // webServer block; the address can be overridden with BASE_URL.
    baseURL: process.env.BASE_URL || 'https://practicesoftwaretesting.com',
    testIdAttribute: 'data-test', // the site marks elements with data-test
    trace: 'on-first-retry',      // trace of exactly the attempt that failed
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  ],
});
