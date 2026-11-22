import { test, expect } from './fixtures';
import { ProductPage } from './pages/product-page';

test.describe('Product browsing', () => {
  test('Products are displayed on the home page', async ({ page }) => {
    const productPage = new ProductPage(page);
    await productPage.open();

    await expect(productPage.productNames.first()).toBeVisible();
    expect(await productPage.productNames.count()).toBeGreaterThan(0);
  });

  test('User can sort products by name A-Z', async ({ page }) => {
    const productPage = new ProductPage(page);
    await productPage.open();
    await expect(productPage.productNames.first()).toBeVisible();

    await productPage.sortByNameAsc();

    // The list is re-rendered after the request, so retry until it is ordered.
    await expect(async () => {
      const names = (await productPage.productNames.allTextContents()).map((n) => n.trim());
      expect(names.length).toBeGreaterThan(0);
      expect(names).toEqual([...names].sort());
    }).toPass({ timeout: 10_000 });
  });

  test('User can search for a product by name', async ({ page }) => {
    const productPage = new ProductPage(page);
    await productPage.open();
    await expect(productPage.productNames.first()).toBeVisible();

    await productPage.search('Hammer');

    // Search is not exact (it also returns related tools), so assert that the
    // result list contains the term rather than that every item matches it.
    await expect(page.getByTestId('search-caption')).toContainText('Hammer');
    await expect(productPage.productNames.filter({ hasText: /hammer/i }).first()).toBeVisible();
  });
});
