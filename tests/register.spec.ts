import { test, expect } from './fixtures';
import { generateUser } from './data/user-factory';
import { RegisterPage } from './pages/register-page';

test('New user can register and is redirected to login', async ({ page }) => {
  const registerPage = new RegisterPage(page);

  await registerPage.open();
  await registerPage.register(generateUser());

  await expect(page).toHaveURL(/\/auth\/login/, { timeout: 15_000 });
});
