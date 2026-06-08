import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import OrderForm from "../../components/Sd/OrderForm";
import OrdersTable from "../../components/Sd/OrdersTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { ClientRecord } from "./ClientsPage";
import "./orders-page.css";

export type OrderRecord = {
  id?: number;
  clienteId: string;
  numeroPedido: string;
  dataPedido: string;
  dataEntrega: string;
  valorTotal: string;
  descontoTotal: string;
  condicoesPagamento: string;
  status: string;
  observacoes: string;
  createdAt?: string;
};

type OrdersPageProps = {
  embedded?: boolean;
};

const ordersResource = {
  schema: "sd",
  entity: "pedidos",
  label: "Pedidos",
  description: "Pedidos comerciais e operacionais.",
} as const;

const clientsResource = {
  schema: "sd",
  entity: "clientes",
  label: "Clientes",
  description: "Base comercial de clientes.",
} as const;

const emptyOrder: OrderRecord = {
  clienteId: "",
  numeroPedido: "",
  dataPedido: "",
  dataEntrega: "",
  valorTotal: "0",
  descontoTotal: "0",
  condicoesPagamento: "",
  status: "aberto",
  observacoes: "",
};

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

function normalizeClient(data: Record<string, unknown>): ClientRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    parceiroId: data.parceiro == null ? "" : String(data.parceiro),
    classificacao: String(data.classificacao ?? "lead"),
    origem: String(data.origem ?? ""),
    website: String(data.website ?? ""),
    faturamentoAnual:
      data.faturamentoAnual == null ? "" : String(data.faturamentoAnual),
    numeroFuncionarios:
      data.numeroFuncionarios == null ? "" : String(data.numeroFuncionarios),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: OrderRecord) {
  return {
    cliente: item.clienteId.trim() === "" ? null : Number(item.clienteId),
    numeroPedido: item.numeroPedido.trim() || null,
    dataPedido: item.dataPedido.trim() || null,
    dataEntrega: item.dataEntrega.trim() || null,
    valorTotal:
      item.valorTotal.trim() === ""
        ? null
        : Number(item.valorTotal.replace(",", ".")),
    descontoTotal:
      item.descontoTotal.trim() === ""
        ? null
        : Number(item.descontoTotal.replace(",", ".")),
    condicoesPagamento: item.condicoesPagamento.trim() || null,
    status: item.status.trim() || null,
    observacoes: item.observacoes.trim() || null,
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

export default function OrdersPage({ embedded = false }: OrdersPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<OrderRecord[]>([]);
  const [clients, setClients] = useState<ClientRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<OrderRecord | null>(null);
  const [draft, setDraft] = useState<OrderRecord>(emptyOrder);
  const canRead = canAccessResourceAction(session, ordersResource, "read");
  const canCreate = canAccessResourceAction(session, ordersResource, "create");
  const canUpdate = canAccessResourceAction(session, ordersResource, "update");
  const canDelete = canAccessResourceAction(session, ordersResource, "delete");
  const canReadClients = canAccessResourceAction(session, clientsResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadOrders() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyOrder });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sd", "pedidos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeOrder(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os pedidos."));
    } finally {
      setLoading(false);
    }
  }

  async function loadClients() {
    if (!canReadClients) {
      setClients([]);
      return;
    }

    try {
      const response = await listResource("sd", "clientes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeClient(item as Record<string, unknown>),
          )
        : [];
      setClients(nextItems);
    } catch {
      setClients([]);
    }
  }

  useEffect(() => {
    void loadOrders();
  }, [canRead]);

  useEffect(() => {
    void loadClients();
  }, [canReadClients]);

  const clientOptions = useMemo(() => clients, [clients]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.clienteId,
        item.numeroPedido,
        item.status,
        item.condicoesPagamento,
        item.observacoes,
        item.dataPedido,
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
    setDraft({ ...emptyOrder });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: OrderRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: OrderRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar pedidos."
          : "Seu perfil nao possui permissao para criar pedidos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sd", "pedidos", selected.id, payload)
        : await createResource("sd", "pedidos", payload);

      const saved = normalizeOrder(response.data as Record<string, unknown>);
      await loadOrders();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Pedido atualizado com sucesso."
          : "Pedido criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o pedido."
            : "Nao foi possivel criar o pedido.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: OrderRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir pedidos.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o pedido para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o pedido "${item.numeroPedido || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sd", "pedidos", item.id);
      await loadOrders();
      setSuccess("Pedido excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o pedido."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "orders-page orders-page--embedded" : "orders-page"}>
      {!embedded ? (
        <header className="orders-page__header">
          <div>
            <span className="orders-page__eyebrow">SD</span>
            <h2 className="orders-page__title">Pedidos</h2>
            <p className="orders-page__subtitle">
              Registre pedidos comerciais vinculados a clientes do CRM e
              acompanhe status, condições de pagamento e cronograma de entrega.
            </p>
          </div>

          <div className="orders-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por cliente, numero, status, pagamento ou observacoes"
              className="orders-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="orders-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo pedido
            </button>
          </div>
        </header>
      ) : (
        <div className="orders-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por cliente, numero, status, pagamento ou observacoes"
            className="orders-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="orders-page__toolbar-actions">
            <button
              type="button"
              className="orders-page__ghost"
              onClick={() => void loadOrders()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="orders-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo pedido
            </button>
          </div>
        </div>
      )}

      {error ? <div className="orders-page__alert">{error}</div> : null}
      {success ? (
        <div className="orders-page__alert orders-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="orders-page__alert orders-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="orders-page__layout">
        <OrdersTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <OrderForm
          value={draft}
          editing={!!selected}
          clients={clientOptions}
          canReadClients={canReadClients}
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
