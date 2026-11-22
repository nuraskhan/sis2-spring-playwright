import { test, expect } from './fixtures';
import { LoginPage } from './pages/login-page';

test.describe('Login', () => {
  test('Valid credentials redirect to account page', async ({ page, customer }) => {
    const loginPage = new LoginPage(page);
    await loginPage.open();
    await loginPage.signIn(customer.email, customer.password);

    await expect(page).toHaveURL(/\/account/, { timeout: 10_000 });
  });

  test('Invalid credentials show error message', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.open();
    await loginPage.signIn('invalid@test.com', 'wrongpassword');

    await expect(loginPage.errorMessage).toBeVisible();
  });
});
