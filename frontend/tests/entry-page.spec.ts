import { expect, test } from "@playwright/test";

test("landing page renders module cards and primary actions", async ({ page }) => {
  await page.goto("/");

  await expect(page.locator(".landing")).toBeVisible();
  await expect(page.locator(".landing-hero h1")).toBeVisible();
  await expect(page.locator(".landing-module-card")).toHaveCount(16);
  await expect(page.getByRole("link", { name: /iniciar demonstra/i })).toBeVisible();
  await expect(page.getByRole("link", { name: /agendar demo/i })).toBeVisible();
});
