import { test, expect } from './fixtures';

test('User can submit a contact message successfully', async ({ page }) => {
  await page.goto('/contact');

  await page.getByTestId('first-name').fill('Test');
  await page.getByTestId('last-name').fill('User');
  await page.getByTestId('email').fill('testuser@test.com');
  await page.getByTestId('subject').selectOption('webmaster');
  // The form requires at least 50 characters.
  await page.getByTestId('message').fill(
    'This is a detailed test message for the contact form that exceeds the minimum fifty character requirement for validation.',
  );
  await page.getByTestId('contact-submit').click();

  await expect(page.locator('.alert-success')).toBeVisible({ timeout: 10_000 });
});
