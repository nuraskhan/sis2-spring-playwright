import type { Locator, Page } from '@playwright/test';

export class ProductPage {
  readonly productNames: Locator;

  constructor(private readonly page: Page) {
    this.productNames = page.getByTestId('product-name');
  }

  async open() {
    await this.page.goto('/');
  }

  async clickFirstProduct() {
    await this.productNames.first().click();
  }

  async addToCart() {
    await this.page.getByTestId('add-to-cart').click();
  }

  async search(query: string) {
    await this.page.getByTestId('search-query').fill(query);
    await this.page.getByTestId('search-submit').click();
  }

  async sortByNameAsc() {
    await this.page.getByTestId('sort').selectOption('name,asc');
  }
}
