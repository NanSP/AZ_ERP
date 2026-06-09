import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import PurchaseItemForm from "../../components/Mm/PurchaseItemForm";
import PurchaseItemsTable from "../../components/Mm/PurchaseItemsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { Product } from "../Core/ProductsPage";
import type { PurchaseRecord } from "./PurchasesPage";
import "./purchase-items-page.css";

export type PurchaseItemRecord = {
  id?: number;
  compraId: string;
  produtoId: string;
  quantidade: string;
  valorUnitario: string;
  valorTotal: string;
  quantidadeRecebida: string;
  createdAt?: string;
};

type PurchaseItemsPageProps = {
  embedded?: boolean;
};

const purchaseItemsResource = {
  schema: "mm",
  entity: "compraItens",
  label: "Itens de Compra",
  description: "Itens vinculados as compras.",
} as const;

const purchasesResource = {
  schema: "mm",
  entity: "compras",
  label: "Compras",
  description: "Processos de compra e aquisicao.",
} as const;

const productsResource = {
  schema: "core",
  entity: "produtos",
  label: "Produtos",
  description: "Catalogo de produtos e itens.",
} as const;

const emptyPurchaseItem: PurchaseItemRecord = {
  compraId: "",
  produtoId: "",
  quantidade: "",
  valorUnitario: "",
  valorTotal: "",
  quantidadeRecebida: "",
};

function normalizePurchaseItem(
  data: Record<string, unknown>,
): PurchaseItemRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    compraId: data.compras == null ? "" : String(data.compras),
    produtoId: data.produtos == null ? "" : String(data.produtos),
    quantidade: data.quantidade == null ? "" : String(data.quantidade),
    valorUnitario: data.valorUnitario == null ? "" : String(data.valorUnitario),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    quantidadeRecebida:
      data.quantidadeRecebida == null ? "" : String(data.quantidadeRecebida),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizePurchase(data: Record<string, unknown>): PurchaseRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    fornecedorId: data.fornecedorId == null ? "" : String(data.fornecedorId),
    dataPedido: data.dataPedido == null ? "" : String(data.dataPedido),
    dataPrevistaEntrega:
      data.dataPrevistaEntrega == null ? "" : String(data.dataPrevistaEntrega),
    dataEntrega: data.dataEntrega == null ? "" : String(data.dataEntrega),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    condicoesPagamento: String(data.condicoesPagamento ?? ""),
    status: String(data.status ?? "aberto"),
    observacoes: String(data.observacoes ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizeProduct(data: Record<string, unknown>): Product {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    codigoBarras: String(data.codigoBarras ?? ""),
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    tipoItem: String(data.tipoItem ?? "produto"),
    unidadeMedida: String(data.unidadeMedida ?? ""),
    ncm: String(data.ncm ?? ""),
    cest: String(data.cest ?? ""),
    pesoBruto: data.pesoBruto == null ? "" : String(data.pesoBruto ?? ""),
    pesoLiquido: data.pesoLiquido == null ? "" : String(data.pesoLiquido ?? ""),
    origem: String(data.origem ?? ""),
    situacao: String(data.situacao ?? "ativo"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: PurchaseItemRecord) {
  function toNumberOrNull(value: string) {
    const normalized = value.trim();

    if (normalized === "") {
      return null;
    }

    return Number(normalized.replace(",", "."));
  }

  return {
    compras: item.compraId.trim() === "" ? null : Number(item.compraId),
    produtos: item.produtoId.trim() === "" ? null : Number(item.produtoId),
    quantidade: toNumberOrNull(item.quantidade),
    valorUnitario: toNumberOrNull(item.valorUnitario),
    quantidadeRecebida: toNumberOrNull(item.quantidadeRecebida),
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message =
      error.response?.data?.message ?? error.response?.data?.error;

    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

export default function PurchaseItemsPage({
  embedded = false,
}: PurchaseItemsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<PurchaseItemRecord[]>([]);
  const [purchases, setPurchases] = useState<PurchaseRecord[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<PurchaseItemRecord | null>(null);
  const [draft, setDraft] = useState<PurchaseItemRecord>(emptyPurchaseItem);
  const canRead = canAccessResourceAction(
    session,
    purchaseItemsResource,
    "read",
  );
  const canCreate = canAccessResourceAction(
    session,
    purchaseItemsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    purchaseItemsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    purchaseItemsResource,
    "delete",
  );
  const canReadPurchases = canAccessResourceAction(
    session,
    purchasesResource,
    "read",
  );
  const canReadProducts = canAccessResourceAction(
    session,
    productsResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadItems = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyPurchaseItem });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("mm", "compraItens");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizePurchaseItem(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possivel carregar os itens de compra."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadPurchases = useCallback(async () => {
    if (!canReadPurchases) {
      setPurchases([]);
      return;
    }

    try {
      const response = await listResource("mm", "compras");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizePurchase(item as Record<string, unknown>),
          )
        : [];
      setPurchases(nextItems);
    } catch {
      setPurchases([]);
    }
  }, [canReadPurchases]);

  const loadProducts = useCallback(async () => {
    if (!canReadProducts) {
      setProducts([]);
      return;
    }

    try {
      const response = await listResource("core", "produtos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeProduct(item as Record<string, unknown>),
          )
        : [];
      setProducts(nextItems);
    } catch {
      setProducts([]);
    }
  }, [canReadProducts]);

  useEffect(() => {
    void loadItems();
  }, [loadItems]);

  useEffect(() => {
    void loadPurchases();
  }, [loadPurchases]);

  useEffect(() => {
    void loadProducts();
  }, [loadProducts]);

  const purchaseOptions = useMemo(
    () =>
      purchases.filter(
        (purchase) =>
          purchase.status.trim().toLowerCase() !== "cancelado" &&
          purchase.status.trim().toLowerCase() !== "recebido",
      ),
    [purchases],
  );

  const productOptions = useMemo(
    () =>
      products.filter(
        (product) =>
          product.situacao === "ativo" && product.tipoItem !== "servico",
      ),
    [products],
  );

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.compraId,
        item.produtoId,
        item.quantidade,
        item.valorUnitario,
        item.valorTotal,
        item.quantidadeRecebida,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyPurchaseItem });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: PurchaseItemRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: PurchaseItemRecord) {
    const quantidade = Number((next.quantidade || "").replace(",", "."));
    const valorUnitario = Number((next.valorUnitario || "").replace(",", "."));
    const valorTotal =
      Number.isFinite(quantidade) && Number.isFinite(valorUnitario)
        ? (Math.round(quantidade * valorUnitario * 100) / 100).toString()
        : "";

    setDraft({
      ...next,
      valorTotal,
    });
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar itens de compra."
          : "Seu perfil não possui permissão para criar itens de compra.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("mm", "compraItens", selected.id, payload)
        : await createResource("mm", "compraItens", payload);

      const saved = normalizePurchaseItem(
        response.data as Record<string, unknown>,
      );
      await loadItems();
      await loadPurchases();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Item de compra atualizado com sucesso."
          : "Item de compra criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o item de compra."
            : "Não foi possível criar o item de compra.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: PurchaseItemRecord) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir itens de compra.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o item de compra para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o item de compra "${item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("mm", "compraItens", item.id);
      await loadItems();
      await loadPurchases();
      setSuccess("Item de compra excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível excluir o item de compra."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "purchase-items-page purchase-items-page--embedded"
          : "purchase-items-page"
      }
    >
      {!embedded ? (
        <header className="purchase-items-page__header">
          <div>
            <span className="purchase-items-page__eyebrow">MM</span>
            <h2 className="purchase-items-page__title">Itens de Compra</h2>
            <p className="purchase-items-page__subtitle">
              Vincule produtos as compras, acompanhe quantidades recebidas e
              consolide o valor total de cada pedido.
            </p>
          </div>

          <div className="purchase-items-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por compra, produto, quantidade ou valores"
              className="purchase-items-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="purchase-items-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo item
            </button>
          </div>
        </header>
      ) : (
        <div className="purchase-items-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por compra, produto, quantidade ou valores"
            className="purchase-items-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="purchase-items-page__toolbar-actions">
            <button
              type="button"
              className="purchase-items-page__ghost"
              onClick={() => void loadItems()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="purchase-items-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo item
            </button>
          </div>
        </div>
      )}

      {error ? <div className="purchase-items-page__alert">{error}</div> : null}
      {success ? (
        <div className="purchase-items-page__alert purchase-items-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="purchase-items-page__alert purchase-items-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="purchase-items-page__layout">
        <PurchaseItemsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <PurchaseItemForm
          value={draft}
          editing={!!selected}
          purchases={purchaseOptions}
          products={productOptions}
          canReadPurchases={canReadPurchases}
          canReadProducts={canReadProducts}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
