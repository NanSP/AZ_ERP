import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import OrderItemForm from "../../components/Sd/OrderItemForm";
import OrderItemsTable from "../../components/Sd/OrderItemsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { Product } from "../Core/ProductsPage";
import type { OrderRecord } from "./OrdersPage";
import "./order-items-page.css";

export type OrderItemRecord = {
  id?: number;
  pedidoId: string;
  produtoId: string;
  quantidade: string;
  valorUnitario: string;
  valorTotal: string;
  desconto: string;
  createdAt?: string;
};

type OrderItemsPageProps = {
  embedded?: boolean;
};

const orderItemsResource = {
  schema: "sd",
  entity: "pedidoItens",
  label: "Itens do Pedido",
  description: "Itens vinculados aos pedidos.",
} as const;

const ordersResource = {
  schema: "sd",
  entity: "pedidos",
  label: "Pedidos",
  description: "Pedidos comerciais e operacionais.",
} as const;

const productsResource = {
  schema: "core",
  entity: "produtos",
  label: "Produtos",
  description: "Catalogo de produtos e itens.",
} as const;

const emptyOrderItem: OrderItemRecord = {
  pedidoId: "",
  produtoId: "",
  quantidade: "",
  valorUnitario: "",
  valorTotal: "",
  desconto: "",
};

function normalizeOrderItem(data: Record<string, unknown>): OrderItemRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    pedidoId: data.pedido == null ? "" : String(data.pedido),
    produtoId: data.produto == null ? "" : String(data.produto),
    quantidade: data.quantidade == null ? "" : String(data.quantidade),
    valorUnitario:
      data.valorUnitario == null ? "" : String(data.valorUnitario),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    desconto: data.desconto == null ? "" : String(data.desconto),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizeOrder(data: Record<string, unknown>): OrderRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    clienteId: data.cliente == null ? "" : String(data.cliente),
    numeroPedido: String(data.numeroPedido ?? ""),
    dataPedido: data.dataPedido == null ? "" : String(data.dataPedido),
    dataEntrega: data.dataEntrega == null ? "" : String(data.dataEntrega),
    valorTotal: data.valorTotal == null ? "0" : String(data.valorTotal),
    descontoTotal:
      data.descontoTotal == null ? "0" : String(data.descontoTotal),
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
    pesoBruto: data.pesoBruto == null ? "" : String(data.pesoBruto),
    pesoLiquido: data.pesoLiquido == null ? "" : String(data.pesoLiquido),
    origem: data.origem == null ? "0" : String(data.origem),
    situacao: String(data.situacao ?? "ativo"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: OrderItemRecord) {
  function toNumberOrNull(value: string) {
    const normalized = value.trim();

    if (normalized === "") {
      return null;
    }

    return Number(normalized.replace(",", "."));
  }

  return {
    pedido: item.pedidoId.trim() === "" ? null : Number(item.pedidoId),
    produto: item.produtoId.trim() === "" ? null : Number(item.produtoId),
    quantidade: toNumberOrNull(item.quantidade),
    valorUnitario: toNumberOrNull(item.valorUnitario),
    desconto: toNumberOrNull(item.desconto),
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

export default function OrderItemsPage({
  embedded = false,
}: OrderItemsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<OrderItemRecord[]>([]);
  const [orders, setOrders] = useState<OrderRecord[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<OrderItemRecord | null>(null);
  const [draft, setDraft] = useState<OrderItemRecord>(emptyOrderItem);
  const canRead = canAccessResourceAction(session, orderItemsResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    orderItemsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    orderItemsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    orderItemsResource,
    "delete",
  );
  const canReadOrders = canAccessResourceAction(session, ordersResource, "read");
  const canReadProducts = canAccessResourceAction(
    session,
    productsResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadOrderItems() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyOrderItem });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sd", "pedidoItens");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeOrderItem(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os itens do pedido."),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadOrders() {
    if (!canReadOrders) {
      setOrders([]);
      return;
    }

    try {
      const response = await listResource("sd", "pedidos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeOrder(item as Record<string, unknown>),
          )
        : [];
      setOrders(nextItems);
    } catch {
      setOrders([]);
    }
  }

  async function loadProducts() {
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
  }

  useEffect(() => {
    void loadOrderItems();
  }, [canRead]);

  useEffect(() => {
    void loadOrders();
  }, [canReadOrders]);

  useEffect(() => {
    void loadProducts();
  }, [canReadProducts]);

  const orderOptions = useMemo(
    () =>
      orders.filter(
        (order) =>
          order.status.trim().toLowerCase() !== "faturado" &&
          order.status.trim().toLowerCase() !== "cancelado",
      ),
    [orders],
  );

  const productOptions = useMemo(
    () =>
      products.filter(
        (product) =>
          product.situacao === "ativo" &&
          product.tipoItem !== "insumo" &&
          product.tipoItem !== "embalagem",
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
        item.pedidoId,
        item.produtoId,
        item.quantidade,
        item.valorUnitario,
        item.valorTotal,
        item.desconto,
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
    setDraft({ ...emptyOrderItem });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: OrderItemRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: OrderItemRecord) {
    const quantidade = Number((next.quantidade || "").replace(",", "."));
    const valorUnitario = Number((next.valorUnitario || "").replace(",", "."));
    const desconto = Number((next.desconto || "").replace(",", "."));
    const valorTotal =
      Number.isFinite(quantidade) &&
      Number.isFinite(valorUnitario) &&
      Number.isFinite(desconto)
        ? (Math.round((quantidade * valorUnitario - desconto) * 100) / 100).toString()
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
          ? "Seu perfil nao possui permissao para atualizar itens do pedido."
          : "Seu perfil nao possui permissao para criar itens do pedido.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sd", "pedidoItens", selected.id, payload)
        : await createResource("sd", "pedidoItens", payload);

      const saved = normalizeOrderItem(response.data as Record<string, unknown>);
      await loadOrderItems();
      await loadOrders();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Item do pedido atualizado com sucesso."
          : "Item do pedido criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o item do pedido."
            : "Nao foi possivel criar o item do pedido.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: OrderItemRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir itens do pedido.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o item do pedido para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o item do pedido "${item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sd", "pedidoItens", item.id);
      await loadOrderItems();
      await loadOrders();
      setSuccess("Item do pedido excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o item do pedido."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "order-items-page order-items-page--embedded"
          : "order-items-page"
      }
    >
      {!embedded ? (
        <header className="order-items-page__header">
          <div>
            <span className="order-items-page__eyebrow">SD</span>
            <h2 className="order-items-page__title">Itens do Pedido</h2>
            <p className="order-items-page__subtitle">
              Detalhe produtos comercializados, quantidades, descontos e valor
              total por item, refletindo automaticamente nos totais do pedido.
            </p>
          </div>

          <div className="order-items-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por pedido, produto, quantidade, valor ou desconto"
              className="order-items-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="order-items-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo item
            </button>
          </div>
        </header>
      ) : (
        <div className="order-items-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por pedido, produto, quantidade, valor ou desconto"
            className="order-items-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="order-items-page__toolbar-actions">
            <button
              type="button"
              className="order-items-page__ghost"
              onClick={() => void loadOrderItems()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="order-items-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo item
            </button>
          </div>
        </div>
      )}

      {error ? <div className="order-items-page__alert">{error}</div> : null}
      {success ? (
        <div className="order-items-page__alert order-items-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="order-items-page__alert order-items-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="order-items-page__layout">
        <OrderItemsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <OrderItemForm
          value={draft}
          editing={!!selected}
          orders={orderOptions}
          products={productOptions}
          canReadOrders={canReadOrders}
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
