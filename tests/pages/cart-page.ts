import type { Locator, Page } from '@playwright/test';

export class CartPage {
  readonly cartQuantity: Locator;
  readonly cartItems: Locator;

  constructor(private readonly page: Page) {
    this.cartQuantity = page.getByTestId('cart-quantity');
    this.cartItems = page.getByTestId('product-title');
  }

  async openCart() {
    await this.cartQuantity.click();
  }

  async proceedToCheckout() {
    await this.page.getByTestId('proceed-1').click();
  }
}
