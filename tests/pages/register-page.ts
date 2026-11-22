import type { Page } from '@playwright/test';
import type { UserData } from '../data/user-factory';

export class RegisterPage {
  constructor(private readonly page: Page) {}

  async open() {
    await this.page.goto('/auth/register');
  }

  async register(user: UserData) {
    const field = (id: string) => this.page.getByTestId(id);
    await field('first-name').fill(user.firstName);
    await field('last-name').fill(user.lastName);
    await field('dob').fill(user.dob);
    await field('country').selectOption(user.country);
    await field('postal_code').fill(user.postcode);
    await field('house_number').fill(user.houseNumber);
    await field('street').fill(user.street);
    await field('city').fill(user.city);
    await field('state').fill(user.state);
    await field('phone').fill(user.phone);
    await field('email').fill(user.email);
    await field('password').fill(user.password);
    await field('register-submit').click();
  }
}
