import type { Locator, Page } from '@playwright/test';

export class LoginPage {
  readonly errorMessage: Locator;

  constructor(private readonly page: Page) {
    this.errorMessage = page.getByTestId('login-error');
  }

  async open() {
    await this.page.goto('/auth/login');
  }

  async signIn(email: string, password: string) {
    await this.page.getByTestId('email').fill(email);
    await this.page.getByTestId('password').fill(password);
    await this.page.getByTestId('login-submit').click();
  }
}
