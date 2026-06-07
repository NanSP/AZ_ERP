import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import OrdersTable from "../../components/Sm/OrdersTable";
import OrderForm from "../../components/Sm/OrderForm";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./orders-page.css";

export type PartnerOption = {
  id: number;
  label: string;
};

export type ProductOption = {
  id: number;
  label: string;
};

export type EmployeeOption = {
  id: number;
  label: string;
};

export type RelatedAccess = "idle" | "loaded" | "unavailable";

export type Order = {
  id?: number;
  numeroOs: string;
  cliente: string;
  produto: string;
  tipoServico: string;
  descricaoProblema: string;
  prioridade: string;
  dataAbertura: string;
  dataAgendamento: string;
  dataInicio: string;
  dataFim: string;
  tecnico: string;
  status: string;
  createdAt?: string;
};

type OrdersPageProps = {
  embedded?: boolean;
};

const ordersResource = {
  schema: "sm",
  entity: "ordensServico",
  label: "Ordens de Servico",
  description: "Ordens e execucao de servicos.",
} as const;

const partnersResource = {
  schema: "core",
  entity: "parceiros",
  label: "Parceiros",
  description: "Clientes e parceiros.",
} as const;

const productsResource = {
  schema: "core",
  entity: "produtos",
  label: "Produtos",
  description: "Produtos e itens.",
} as const;

const employeesResource = {
  schema: "rh",
  entity: "colaboradores",
  label: "Colaboradores",
  description: "Tecnicos e equipe.",
} as const;

const emptyOrder: Order = {
  numeroOs: "",
  cliente: "",
  produto: "",
  tipoServico: "",
  descricaoProblema: "",
  prioridade: "normal",
  dataAbertura: "",
  dataAgendamento: "",
  dataInicio: "",
  dataFim: "",
  tecnico: "",
  status: "aberta",
};

function normalizeDateTime(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 16 ? raw.slice(0, 16) : raw;
}

function normalizeDate(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 10 ? raw.slice(0, 10) : raw;
}

function normalizeOrder(data: Record<string, unknown>): Order {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    numeroOs: String(data.numeroOs ?? ""),
    cliente: data.cliente == null ? "" : String(data.cliente),
    produto: data.produto == null ? "" : String(data.produto),
    tipoServico: String(data.tipoServico ?? ""),
    descricaoProblema: String(data.descricaoProblema ?? ""),
    prioridade: String(data.prioridade ?? "normal"),
    dataAbertura: normalizeDateTime(data.dataAbertura),
    dataAgendamento: normalizeDate(data.dataAgendamento),
    dataInicio: normalizeDateTime(data.dataInicio),
    dataFim: normalizeDateTime(data.dataFim),
    tecnico: data.tecnico == null ? "" : String(data.tecnico),
    status: String(data.status ?? "aberta"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(order: Order) {
  return {
    numeroOs: order.numeroOs.trim() || null,
    cliente: order.cliente.trim() === "" ? null : Number(order.cliente),
    produto: order.produto.trim() === "" ? null : Number(order.produto),
    tipoServico: order.tipoServico.trim() || null,
    descricaoProblema: order.descricaoProblema.trim() || null,
    prioridade: order.prioridade.trim() || null,
    dataAbertura: order.dataAbertura.trim() || null,
    dataAgendamento: order.dataAgendamento.trim() || null,
    dataInicio: order.dataInicio.trim() || null,
    dataFim: order.dataFim.trim() || null,
    tecnico: order.tecnico.trim() === "" ? null : Number(order.tecnico),
    status: order.status.trim() || null,
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  return fallback;
}

function mapOption(
  item: Record<string, unknown>,
  fallbackLabel: string,
): { id: number; label: string } | null {
  if (typeof item.id !== "number") {
    return null;
  }

  return {
    id: item.id,
    label: `${String(item.nome ?? item.codigo ?? fallbackLabel)} (#${String(item.id)})`,
  };
}

export default function OrdersPage({ embedded = false }: OrdersPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Order[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Order | null>(null);
  const [draft, setDraft] = useState<Order>(emptyOrder);
  const [partnerOptions, setPartnerOptions] = useState<PartnerOption[]>([]);
  const [partnerAccess, setPartnerAccess] = useState<RelatedAccess>("idle");
  const [productOptions, setProductOptions] = useState<ProductOption[]>([]);
  const [productAccess, setProductAccess] = useState<RelatedAccess>("idle");
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<RelatedAccess>("idle");
  const canRead = canAccessResourceAction(session, ordersResource, "read");
  const canCreate = canAccessResourceAction(session, ordersResource, "create");
  const canUpdate = canAccessResourceAction(session, ordersResource, "update");
  const canDelete = canAccessResourceAction(session, ordersResource, "delete");
  const canReadPartners = canAccessResourceAction(
    session,
    partnersResource,
    "read",
  );
  const canReadProducts = canAccessResourceAction(
    session,
    productsResource,
    "read",
  );
  const canReadEmployees = canAccessResourceAction(
    session,
    employeesResource,
    "read",
  );
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
      const response = await listResource("sm", "ordensServico");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeOrder(item as Record<string, unknown>))
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar as ordens de servico."),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadPartners() {
    if (!canReadPartners) {
      setPartnerOptions([]);
      setPartnerAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("core", "parceiros");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => mapOption(item as Record<string, unknown>, "Cliente"))
            .filter((item): item is PartnerOption => item !== null)
        : [];
      setPartnerOptions(nextItems);
      setPartnerAccess("loaded");
    } catch {
      setPartnerOptions([]);
      setPartnerAccess("unavailable");
    }
  }

  async function loadProducts() {
    if (!canReadProducts) {
      setProductOptions([]);
      setProductAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("core", "produtos");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => mapOption(item as Record<string, unknown>, "Produto"))
            .filter((item): item is ProductOption => item !== null)
        : [];
      setProductOptions(nextItems);
      setProductAccess("loaded");
    } catch {
      setProductOptions([]);
      setProductAccess("unavailable");
    }
  }

  async function loadEmployees() {
    if (!canReadEmployees) {
      setEmployeeOptions([]);
      setEmployeeAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("rh", "colaboradores");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => mapOption(item as Record<string, unknown>, "Tecnico"))
            .filter((item): item is EmployeeOption => item !== null)
        : [];
      setEmployeeOptions(nextItems);
      setEmployeeAccess("loaded");
    } catch {
      setEmployeeOptions([]);
      setEmployeeAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadOrders();
  }, [canRead]);

  useEffect(() => {
    void loadPartners();
  }, [canReadPartners]);

  useEffect(() => {
    void loadProducts();
  }, [canReadProducts]);

  useEffect(() => {
    void loadEmployees();
  }, [canReadEmployees]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.numeroOs, item.tipoServico, item.status, item.descricaoProblema]
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

  function handleSelect(item: Order) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Order) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar ordens de servico."
          : "Seu perfil nao possui permissao para criar ordens de servico.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sm", "ordensServico", selected.id, payload)
        : await createResource("sm", "ordensServico", payload);

      const saved = normalizeOrder(response.data as Record<string, unknown>);
      await loadOrders();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Ordem de servico atualizada com sucesso."
          : "Ordem de servico criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a ordem de servico."
            : "Nao foi possivel criar a ordem de servico.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Order) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir ordens de servico.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a ordem de servico para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a ordem de servico "${item.numeroOs || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sm", "ordensServico", item.id);
      await loadOrders();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyOrder });
      }

      setSuccess("Ordem de servico excluida com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel excluir a ordem de servico.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "orders-page orders-page--embedded" : "orders-page"}>
      {!embedded ? (
        <header className="orders-page__header">
          <div>
            <span className="orders-page__eyebrow">SM</span>
            <h2 className="orders-page__title">Ordens de Servico</h2>
            <p className="orders-page__subtitle">
              Gerencie cliente, produto, tecnico, agenda e status das ordens de servico.
            </p>
          </div>

          <div className="orders-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por OS, tipo de servico, descricao ou status"
              className="orders-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="orders-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova ordem
            </button>
          </div>
        </header>
      ) : (
        <div className="orders-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por OS, tipo de servico, descricao ou status"
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
              Nova ordem
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
          partnerOptions={partnerOptions}
          productOptions={productOptions}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <OrderForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          partnerOptions={partnerOptions}
          partnerAccess={partnerAccess}
          productOptions={productOptions}
          productAccess={productAccess}
          employeeOptions={employeeOptions}
          employeeAccess={employeeAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
