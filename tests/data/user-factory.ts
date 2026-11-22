import { randomInt } from 'node:crypto';

export interface UserData {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  dob: string;
  country: string;
  postcode: string;
  houseNumber: string;
  street: string;
  city: string;
  state: string;
  phone: string;
}

/**
 * Builds a user that is unique per call, so tests running in parallel
 * (several workers, several CI shards) never collide on the e-mail address.
 * The password is generated because the site rejects well-known ones.
 */
export function generateUser(): UserData {
  const stamp = Date.now();
  const salt = randomInt(100_000, 1_000_000);
  return {
    firstName: 'Test',
    lastName: 'User',
    email: `testuser.${stamp}.${salt}@test.com`,
    password: `Qz9$kL${String(salt).slice(-4)}xR!`,
    dob: '1990-01-01',
    country: 'KZ',
    postcode: '050000',
    houseNumber: '42',
    street: 'Abay Ave',
    city: 'Almaty',
    state: 'Almaty',
    phone: '77001234567',
  };
}
