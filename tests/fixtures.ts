import { test as base, expect } from '@playwright/test';
import { generateUser, type UserData } from './data/user-factory';

const API_URL = process.env.API_URL || 'https://api.practicesoftwaretesting.com';

type Fixtures = {
  /**
   * A customer account that exists only for the current test.
   * It is registered through the REST API before the test starts, so no
   * credentials are stored in the repository and parallel shards never share
   * (and never lock) the same account.
   */
  customer: UserData;
  /**
   * The same customer, already signed in when the first page opens.
   * The token comes from the login API and is placed where the app keeps it
   * (localStorage `auth-token`), so tests that are not about the login form do
   * not have to go through it. The login form itself is covered by login.spec.ts.
   */
  signedInCustomer: UserData;
};

export const test = base.extend<Fixtures>({
  customer: async ({ request }, use) => {
    const user = generateUser();
    const response = await request.post(`${API_URL}/users/register`, {
      headers: { Accept: 'application/json' },
      data: {
        first_name: user.firstName,
        last_name: user.lastName,
        dob: user.dob,
        phone: user.phone,
        email: user.email,
        password: user.password,
        address: {
          street: user.street,
          house_number: user.houseNumber,
          city: user.city,
          state: user.state,
          country: user.country,
          postal_code: user.postcode,
        },
      },
    });
    expect(response.status(), `register ${user.email}: ${await response.text()}`).toBe(201);
    await use(user);
  },

  signedInCustomer: async ({ customer, request, page }, use) => {
    const response = await request.post(`${API_URL}/users/login`, {
      headers: { Accept: 'application/json' },
      data: { email: customer.email, password: customer.password },
    });
    expect(response.status(), `login ${customer.email}: ${await response.text()}`).toBe(200);
    const { access_token: token } = await response.json();
    await page.addInitScript((value) => window.localStorage.setItem('auth-token', value), token);
    await use(customer);
  },
});

export { expect };
