import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import InvoiceForm from "../../components/Sd/InvoiceForm";
import InvoicesTable from "../../components/Sd/InvoicesTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { OrderRecord } from "./OrdersPage";
import "./invoices-page.css";

export type InvoiceRecord = {
  id?: number;
  pedidoId: string;
  numeroFatura: string;
  dataEmissao: string;
  valorTotal: string;
  dataVencimento: string;
  status: string;
  createdAt?: string;
};

type InvoicesPageProps = {
  embedded?: boolean;
};

const invoicesResource = {
  schema: "sd",
  entity: "faturas",
  label: "Faturas",
  description: "Faturamento e cobranca.",
} as const;

const ordersResource = {
  schema: "sd",
  entity: "pedidos",
  label: "Pedidos",
  description: "Pedidos comerciais e operacionais.",
} as const;

const emptyInvoice: InvoiceRecord = {
  pedidoId: "",
  numeroFatura: "",
  dataEmissao: "",
  valorTotal: "",
  dataVencimento: "",
  status: "emitida",
};

function normalizeInvoice(data: Record<string, unknown>): InvoiceRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    pedidoId: data.pedido == null ? "" : String(data.pedido),
    numeroFatura: String(data.numeroFatura ?? ""),
    dataEmissao: data.dataEmissao == null ? "" : String(data.dataEmissao),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    dataVencimento:
      data.dataVencimento == null ? "" : String(data.dataVencimento),
    status: String(data.status ?? "emitida"),
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

function toRequestPayload(item: InvoiceRecord) {
  return {
    pedido: item.pedidoId.trim() === "" ? null : Number(item.pedidoId),
    numeroFatura: item.numeroFatura.trim() || null,
    dataEmissao: item.dataEmissao.trim() || null,
    valorTotal:
      item.valorTotal.trim() === ""
        ? null
        : Number(item.valorTotal.replace(",", ".")),
    dataVencimento: item.dataVencimento.trim() || null,
    status: item.status.trim() || null,
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

export default function InvoicesPage({ embedded = false }: InvoicesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<InvoiceRecord[]>([]);
  const [orders, setOrders] = useState<OrderRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<InvoiceRecord | null>(null);
  const [draft, setDraft] = useState<InvoiceRecord>(emptyInvoice);
  const canRead = canAccessResourceAction(session, invoicesResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    invoicesResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    invoicesResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    invoicesResource,
    "delete",
  );
  const canReadOrders = canAccessResourceAction(
    session,
    ordersResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadInvoices = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyInvoice });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sd", "faturas");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeInvoice(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível carregar as faturas."));
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadOrders = useCallback(async () => {
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
  }, [canReadOrders]);

  useEffect(() => {
    void loadInvoices();
  }, [loadInvoices]);

  useEffect(() => {
    void loadOrders();
  }, [loadOrders]);

  const orderOptions = useMemo(() => {
    const selectedOrderId = selected?.pedidoId ?? "";
    const usedOrderIds = new Set(
      items
        .filter(
          (item) =>
            item.pedidoId !== selectedOrderId &&
            item.status.trim().toLowerCase() !== "cancelada",
        )
        .map((item) => item.pedidoId),
    );

    return orders.filter(
      (order) =>
        order.status.trim().toLowerCase() !== "cancelado" &&
        !usedOrderIds.has(String(order.id ?? "")),
    );
  }, [items, orders, selected?.pedidoId]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.numeroFatura,
        item.status,
        item.pedidoId,
        item.dataEmissao,
        item.dataVencimento,
        item.valorTotal,
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
    setDraft({ ...emptyInvoice });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: InvoiceRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: InvoiceRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar faturas."
          : "Seu perfil não possui permissão para criar faturas.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sd", "faturas", selected.id, payload)
        : await createResource("sd", "faturas", payload);

      const saved = normalizeInvoice(response.data as Record<string, unknown>);
      await loadInvoices();
      await loadOrders();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Fatura atualizada com sucesso."
          : "Fatura criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar a fatura."
            : "Não foi possível criar a fatura.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: InvoiceRecord) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir faturas.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar a fatura para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a fatura "${item.numeroFatura || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sd", "faturas", item.id);
      await loadInvoices();
      await loadOrders();
      setSuccess("Fatura excluída com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir a fatura."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "invoices-page invoices-page--embedded" : "invoices-page"
      }
    >
      {!embedded ? (
        <header className="invoices-page__header">
          <div>
            <span className="invoices-page__eyebrow">SD</span>
            <h2 className="invoices-page__title">Faturas</h2>
            <p className="invoices-page__subtitle">
              Emita e acompanhe faturas vinculadas aos pedidos, controlando
              valor faturado, vencimento e status da cobrança.
            </p>
          </div>

          <div className="invoices-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por numero, pedido, status, emissão ou vencimento"
              className="invoices-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="invoices-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova fatura
            </button>
          </div>
        </header>
      ) : (
        <div className="invoices-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por numero, pedido, status, emissão ou vencimento"
            className="invoices-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="invoices-page__toolbar-actions">
            <button
              type="button"
              className="invoices-page__ghost"
              onClick={() => void loadInvoices()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="invoices-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova fatura
            </button>
          </div>
        </div>
      )}

      {error ? <div className="invoices-page__alert">{error}</div> : null}
      {success ? (
        <div className="invoices-page__alert invoices-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="invoices-page__alert invoices-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="invoices-page__layout">
        <InvoicesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <InvoiceForm
          value={draft}
          editing={!!selected}
          orders={orderOptions}
          canReadOrders={canReadOrders}
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
