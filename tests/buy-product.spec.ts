import { test, expect } from './fixtures';
import { CartPage } from './pages/cart-page';
import { CheckoutPage } from './pages/checkout-page';
import { LoginPage } from './pages/login-page';
import { ProductPage } from './pages/product-page';

test('Logged-in user can add product to cart and complete purchase', async ({ page, customer }) => {
  // Login
  const loginPage = new LoginPage(page);
  await loginPage.open();
  await loginPage.signIn(customer.email, customer.password);
  await expect(page).toHaveURL(/\/account/, { timeout: 10_000 });

  // Browse and add product
  const productPage = new ProductPage(page);
  await productPage.openFromMenu();
  await expect(productPage.productNames.first()).toBeVisible({ timeout: 10_000 });
  await productPage.clickFirstProduct();
  await productPage.addToCart();

  // Verify cart updated
  const cartPage = new CartPage(page);
  await expect(cartPage.cartQuantity).toHaveText('1', { timeout: 10_000 });

  // Go to cart and checkout
  await cartPage.openCart();
  await expect(cartPage.cartItems.first()).toBeVisible();
  await cartPage.proceedToCheckout();

  const checkoutPage = new CheckoutPage(page);

  // Step 2 - already signed in
  await expect(checkoutPage.proceedStep2).toBeVisible({ timeout: 10_000 });
  await checkoutPage.proceedStep2.click();

  // Step 3 - billing address
  await checkoutPage.fillBillingAddress();
  await expect(checkoutPage.proceedStep3).toBeEnabled({ timeout: 10_000 });
  await checkoutPage.proceedStep3.click();

  // Step 4 - payment
  await checkoutPage.fillBankTransferPayment();
  await checkoutPage.finishButton.click();

  await expect(checkoutPage.successMessage).toBeVisible({ timeout: 15_000 });
});
