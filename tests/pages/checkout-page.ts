import type { Locator, Page } from '@playwright/test';

export class CheckoutPage {
  readonly proceedStep2: Locator;
  readonly proceedStep3: Locator;
  readonly finishButton: Locator;
  readonly successMessage: Locator;

  constructor(private readonly page: Page) {
    this.proceedStep2 = page.getByTestId('proceed-2');
    this.proceedStep3 = page.getByTestId('proceed-3');
    this.finishButton = page.getByTestId('finish');
    this.successMessage = page.getByText('Payment was successful');
  }

  async fillBillingAddress() {
    await this.page.getByTestId('country').selectOption({ label: 'Germany' });
    await this.page.getByTestId('postal_code').fill('10115');
    await this.page.getByTestId('house_number').fill('42');
  }

  // The payment fields have no data-test attribute, only placeholders.
  async fillBankTransferPayment() {
    await this.page.getByTestId('payment-method').selectOption({ label: 'Bank Transfer' });
    await this.page.getByPlaceholder('Bank Name').fill('Test Bank');
    await this.page.getByPlaceholder('Account Name').fill('Test User');
    await this.page.getByPlaceholder('Account Number').fill('1234567890');
  }
}
