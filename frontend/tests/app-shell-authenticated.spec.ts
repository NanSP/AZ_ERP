import { expect, test } from "@playwright/test";

const SESSION_KEY = "az_erp_session";

function createTenantSession() {
  return {
    token: "test-token",
    scope: "tenant",
    login: "qa.tenant",
    userId: 101,
    tenantId: 1,
    tenantCode: "demo",
    perfis: ["Administrador"],
    permissoes: [
      "core:parceiros:read",
      "core:parceiros:create",
      "core:parceiros:update",
      "core:parceiros:delete",
    ],
    passwordChangeRequired: false,
  };
}

test.beforeEach(async ({ page }) => {
  await page.addInitScript(
    ({ key, session }) => {
      window.localStorage.setItem(key, JSON.stringify(session));
    },
    { key: SESSION_KEY, session: createTenantSession() },
  );
});

test("authenticated user can access dashboard and core/parceiros workspace", async ({
  page,
}) => {
  await page.route("**/api/core/parceiros", async (route) => {
    await route.fulfill({
      status: 200,
      contentType: "application/json",
      body: JSON.stringify([]),
    });
  });

  await page.goto("/app");
  await expect(page).toHaveURL(/\/app$/);
  await expect(page.locator(".app-shell")).toBeVisible();
  await expect(page.locator(".dashboard-page")).toBeVisible();

  await page.goto("/app/module/core/parceiros");
  await expect(page.locator(".module-workspace")).toBeVisible();
  await expect(page.locator(".partners-page--embedded")).toBeVisible();
  await expect(
    page.getByRole("heading", { name: /parceiros/i }).first(),
  ).toBeVisible();
});

test("authenticated user can create, update and delete parceiro with mocked api", async ({
  page,
}) => {
  let partners = [
    {
      id: 1,
      tipoParceiro: "cliente",
      codigo: "PAR-001",
      nome: "Parceiro Base",
      nomeFantasia: "Base",
      documento: "12345678901",
      tipoPessoa: "F",
      situacao: "ativo",
      limiteCredito: "1000",
      diasPrazo: "30",
      observacoes: "Registro inicial",
      createdAt: "2026-01-01T00:00:00Z",
    },
  ];

  await page.route("**/api/core/parceiros", async (route) => {
    const request = route.request();

    if (request.method() === "GET") {
      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(partners),
      });
      return;
    }

    if (request.method() === "POST") {
      const payload = (await request.postDataJSON()) as Record<string, unknown>;
      const created = {
        id: 2,
        tipoParceiro: String(payload.tipoParceiro ?? "cliente"),
        codigo: String(payload.codigo ?? ""),
        nome: String(payload.nome ?? ""),
        nomeFantasia: String(payload.nomeFantasia ?? ""),
        documento: String(payload.documento ?? ""),
        tipoPessoa: String(payload.tipoPessoa ?? ""),
        situacao: String(payload.situacao ?? "ativo"),
        limiteCredito:
          payload.limiteCredito == null ? "" : String(payload.limiteCredito),
        diasPrazo: payload.diasPrazo == null ? "" : String(payload.diasPrazo),
        observacoes: String(payload.observacoes ?? ""),
        createdAt: "2026-01-02T00:00:00Z",
      };
      partners = [...partners, created];

      await route.fulfill({
        status: 201,
        contentType: "application/json",
        body: JSON.stringify(created),
      });
      return;
    }

    await route.fallback();
  });

  await page.route("**/api/core/parceiros/*", async (route) => {
    const request = route.request();
    const id = Number(route.request().url().split("/").pop());
    const index = partners.findIndex((item) => item.id === id);

    if (request.method() === "PUT") {
      const payload = (await request.postDataJSON()) as Record<string, unknown>;
      const current = partners[index];
      const updated = {
        ...current,
        tipoParceiro: String(payload.tipoParceiro ?? current?.tipoParceiro ?? ""),
        codigo: String(payload.codigo ?? current?.codigo ?? ""),
        nome: String(payload.nome ?? current?.nome ?? ""),
        nomeFantasia: String(payload.nomeFantasia ?? current?.nomeFantasia ?? ""),
        documento: String(payload.documento ?? current?.documento ?? ""),
        tipoPessoa: String(payload.tipoPessoa ?? current?.tipoPessoa ?? ""),
        situacao: String(payload.situacao ?? current?.situacao ?? "ativo"),
        limiteCredito:
          payload.limiteCredito == null
            ? ""
            : String(payload.limiteCredito),
        diasPrazo:
          payload.diasPrazo == null ? "" : String(payload.diasPrazo),
        observacoes: String(payload.observacoes ?? current?.observacoes ?? ""),
      };

      partners = partners.map((item) => (item.id === id ? updated : item));

      await route.fulfill({
        status: 200,
        contentType: "application/json",
        body: JSON.stringify(updated),
      });
      return;
    }

    if (request.method() === "DELETE") {
      partners = partners.filter((item) => item.id !== id);

      await route.fulfill({
        status: 204,
        contentType: "application/json",
        body: "",
      });
      return;
    }

    await route.fallback();
  });

  page.on("dialog", (dialog) => dialog.accept());

  await page.goto("/app/module/core/parceiros");

  await expect(page.locator(".partners-page--embedded")).toBeVisible();
  await expect(page.getByText("Parceiro Base")).toBeVisible();

  await page.getByRole("button", { name: /novo parceiro/i }).click();
  await page.getByLabel("Codigo").fill("PAR-002");
  await page.getByLabel("Nome").fill("Parceiro Playwright");
  await page.getByLabel("Documento").fill("99887766554");
  await page.getByRole("button", { name: /criar parceiro/i }).click();

  await expect(page.getByText("Parceiro criado com sucesso.")).toBeVisible();
  await expect(page.getByText("Parceiro Playwright")).toBeVisible();

  await page
    .locator("tr", { has: page.getByText("Parceiro Playwright") })
    .getByRole("button", { name: /editar/i })
    .click();
  await page.getByLabel("Nome").fill("Parceiro Playwright Atualizado");
  await page.getByRole("button", { name: /salvar alteracoes/i }).click();

  await expect(
    page.getByText("Parceiro atualizado com sucesso."),
  ).toBeVisible();
  await expect(page.getByText("Parceiro Playwright Atualizado")).toBeVisible();

  await page
    .locator("tr", { has: page.getByText("Parceiro Playwright Atualizado") })
    .getByRole("button", { name: /excluir/i })
    .click();

  await expect(page.getByText("Parceiro excluido com sucesso.")).toBeVisible();
  await expect(
    page.getByText("Parceiro Playwright Atualizado"),
  ).not.toBeVisible();
});
