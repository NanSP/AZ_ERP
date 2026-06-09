import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ProductionOrderForm from "../../components/Pp/ProductionOrderForm";
import ProductionOrdersTable from "../../components/Pp/ProductionOrdersTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./production-orders-page.css";

export type ProductOption = {
  id: number;
  label: string;
};

export type ProductAccess = "idle" | "loaded" | "unavailable";

export type ProductionOrder = {
  id?: number;
  numeroOp: string;
  produto: string;
  quantidadePlanejada: string;
  quantidadeProduzida: string;
  dataEmissao: string;
  dataInicio: string;
  dataFim: string;
  dataPrevista: string;
  status: string;
  prioridade: string;
  observacoes: string;
  createdAt?: string;
};

type ProductionOrdersPageProps = {
  embedded?: boolean;
};

const productionOrdersResource = {
  schema: "pp",
  entity: "ordemProducao",
  label: "Ordens de Producao",
  description: "Ordens e execucao de producao.",
} as const;

const productsResource = {
  schema: "core",
  entity: "produtos",
  label: "Produtos",
  description: "Produtos e itens.",
} as const;

const emptyProductionOrder: ProductionOrder = {
  numeroOp: "",
  produto: "",
  quantidadePlanejada: "",
  quantidadeProduzida: "",
  dataEmissao: "",
  dataInicio: "",
  dataFim: "",
  dataPrevista: "",
  status: "planejada",
  prioridade: "1",
  observacoes: "",
};

function normalizeDate(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 10 ? raw.slice(0, 10) : raw;
}

function normalizeProductionOrder(
  data: Record<string, unknown>,
): ProductionOrder {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    numeroOp: String(data.numeroOp ?? ""),
    produto: data.produto == null ? "" : String(data.produto),
    quantidadePlanejada:
      data.quantidadePlanejada == null ? "" : String(data.quantidadePlanejada),
    quantidadeProduzida:
      data.quantidadeProduzida == null ? "" : String(data.quantidadeProduzida),
    dataEmissao: normalizeDate(data.dataEmissao),
    dataInicio: normalizeDate(data.dataInicio),
    dataFim: normalizeDate(data.dataFim),
    dataPrevista: normalizeDate(data.dataPrevista),
    status: String(data.status ?? "planejada"),
    prioridade: data.prioridade == null ? "1" : String(data.prioridade),
    observacoes: String(data.observacoes ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(order: ProductionOrder) {
  return {
    numeroOp: order.numeroOp.trim() || null,
    produto: order.produto.trim() === "" ? null : Number(order.produto),
    quantidadePlanejada: toNullableNumber(order.quantidadePlanejada),
    quantidadeProduzida: toNullableNumber(order.quantidadeProduzida),
    dataEmissao: order.dataEmissao.trim() || null,
    dataInicio: order.dataInicio.trim() || null,
    dataFim: order.dataFim.trim() || null,
    dataPrevista: order.dataPrevista.trim() || null,
    status: order.status.trim() || null,
    prioridade:
      order.prioridade.trim() === "" ? null : Number(order.prioridade),
    observacoes: order.observacoes.trim() || null,
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

function mapProductOption(item: Record<string, unknown>): ProductOption | null {
  if (typeof item.id !== "number") {
    return null;
  }

  return {
    id: item.id,
    label: `${String(item.nome ?? item.codigo ?? "Produto")} (#${String(item.id)})`,
  };
}

export default function ProductionOrdersPage({
  embedded = false,
}: ProductionOrdersPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ProductionOrder[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<ProductionOrder | null>(null);
  const [draft, setDraft] = useState<ProductionOrder>(emptyProductionOrder);
  const [productOptions, setProductOptions] = useState<ProductOption[]>([]);
  const [productAccess, setProductAccess] = useState<ProductAccess>("idle");
  const canRead = canAccessResourceAction(
    session,
    productionOrdersResource,
    "read",
  );
  const canCreate = canAccessResourceAction(
    session,
    productionOrdersResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    productionOrdersResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    productionOrdersResource,
    "delete",
  );
  const canReadProducts = canAccessResourceAction(
    session,
    productsResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadProductionOrders = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyProductionOrder });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("pp", "ordemProducao");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeProductionOrder(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Não foi possível carregar as ordens de produção.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadProducts = useCallback(async () => {
    if (!canReadProducts) {
      setProductOptions([]);
      setProductAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("core", "produtos");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => mapProductOption(item as Record<string, unknown>))
            .filter((item): item is ProductOption => item !== null)
        : [];
      setProductOptions(nextItems);
      setProductAccess("loaded");
    } catch {
      setProductOptions([]);
      setProductAccess("unavailable");
    }
  }, [canReadProducts]);

  useEffect(() => {
    void loadProductionOrders();
  }, [loadProductionOrders]);

  useEffect(() => {
    void loadProducts();
  }, [loadProducts]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.numeroOp, item.status, item.observacoes]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyProductionOrder });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: ProductionOrder) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ProductionOrder) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar ordens de produção."
          : "Seu perfil não possui permissão para criar ordens de produção.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("pp", "ordemProducao", selected.id, payload)
        : await createResource("pp", "ordemProducao", payload);

      const saved = normalizeProductionOrder(
        response.data as Record<string, unknown>,
      );
      await loadProductionOrders();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Ordem de produção atualizada com sucesso."
          : "Ordem de produção criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar a ordem de produção."
            : "Não foi possível criar a ordem de produção.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: ProductionOrder) {
    if (!canDelete) {
      setError(
        "Seu perfil não possui permissão para excluir ordens de produção.",
      );
      return;
    }

    if (!item.id) {
      setError(
        "Não foi possível identificar a ordem de produção para exclusão.",
      );
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a ordem de produção "${item.numeroOp || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("pp", "ordemProducao", item.id);
      await loadProductionOrders();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyProductionOrder });
      }

      setSuccess("Ordem de produção excluída com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível excluir a ordem de produção."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "production-orders-page production-orders-page--embedded"
          : "production-orders-page"
      }
    >
      {!embedded ? (
        <header className="production-orders-page__header">
          <div>
            <span className="production-orders-page__eyebrow">PP</span>
            <h2 className="production-orders-page__title">
              Ordens de Produção
            </h2>
            <p className="production-orders-page__subtitle">
              Planeje, acompanhe quantidades, datas e evolucao operacional da
              produção.
            </p>
          </div>

          <div className="production-orders-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por numero, status ou observacoes"
              className="production-orders-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="production-orders-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova ordem
            </button>
          </div>
        </header>
      ) : (
        <div className="production-orders-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por numero, status ou observacoes"
            className="production-orders-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="production-orders-page__toolbar-actions">
            <button
              type="button"
              className="production-orders-page__ghost"
              onClick={() => void loadProductionOrders()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="production-orders-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova ordem
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="production-orders-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="production-orders-page__alert production-orders-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="production-orders-page__alert production-orders-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="production-orders-page__layout">
        <ProductionOrdersTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          productOptions={productOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ProductionOrderForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          productOptions={productOptions}
          productAccess={productAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
