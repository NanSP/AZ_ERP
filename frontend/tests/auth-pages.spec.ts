import { expect, test } from "@playwright/test";

test("master login page exposes expected fields and navigation", async ({ page }) => {
  await page.goto("/login");

  const authCard = page.locator(".auth-card");
  await expect(page.locator("#master-login")).toBeVisible();
  await expect(page.locator("#master-password")).toBeVisible();
  await authCard.getByRole("link", { name: /ir para o login do tenant/i }).click();
  await expect(page).toHaveURL(/\/tenant-login$/);
});

test("tenant login page exposes expected fields and navigation", async ({ page }) => {
  await page.goto("/tenant-login");

  const authCard = page.locator(".auth-card");
  await expect(page.locator("#tenant-code")).toBeVisible();
  await expect(page.locator("#tenant-login")).toBeVisible();
  await expect(page.locator("#tenant-password")).toBeVisible();
  await authCard.getByRole("link", { name: /ir para o login master/i }).click();
  await expect(page).toHaveURL(/\/login$/);
});

test("change password page renders credential form", async ({ page }) => {
  await page.goto("/change-password");

  await expect(page.locator("#current-password")).toBeVisible();
  await expect(page.locator("#new-password")).toBeVisible();
  await expect(page.getByRole("button", { name: /alterar senha/i })).toBeVisible();
});
