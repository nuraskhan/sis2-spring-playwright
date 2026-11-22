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
});

export { expect };
