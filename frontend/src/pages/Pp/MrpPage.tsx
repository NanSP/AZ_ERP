import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import MrpForm from "../../components/Pp/MrpForm";
import MrpTable from "../../components/Pp/MrpTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./mrp-page.css";

export type ProductOption = {
  id: number;
  label: string;
};

export type ProductAccess = "idle" | "loaded" | "unavailable";

export type MrpItem = {
  id?: number;
  produto: string;
  periodo: string;
  demandaPrevista: string;
  estoqueAtual: string;
  estoqueSeguranca: string;
  necessidadeCompra: string;
  necessidadeProducao: string;
  dataNecessidade: string;
  createdAt?: string;
};

type MrpPageProps = {
  embedded?: boolean;
};

const mrpResource = {
  schema: "pp",
  entity: "mrp",
  label: "MRP",
  description: "Planejamento de necessidades de materiais.",
} as const;

const productsResource = {
  schema: "core",
  entity: "produtos",
  label: "Produtos",
  description: "Produtos e itens.",
} as const;

const emptyMrp: MrpItem = {
  produto: "",
  periodo: "",
  demandaPrevista: "",
  estoqueAtual: "",
  estoqueSeguranca: "",
  necessidadeCompra: "",
  necessidadeProducao: "",
  dataNecessidade: "",
};

function normalizeDate(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 10 ? raw.slice(0, 10) : raw;
}

function normalizeMrp(data: Record<string, unknown>): MrpItem {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    produto: data.produto == null ? "" : String(data.produto),
    periodo: normalizeDate(data.periodo),
    demandaPrevista:
      data.demandaPrevista == null ? "" : String(data.demandaPrevista),
    estoqueAtual: data.estoqueAtual == null ? "" : String(data.estoqueAtual),
    estoqueSeguranca:
      data.estoqueSeguranca == null ? "" : String(data.estoqueSeguranca),
    necessidadeCompra:
      data.necessidadeCompra == null ? "" : String(data.necessidadeCompra),
    necessidadeProducao:
      data.necessidadeProducao == null ? "" : String(data.necessidadeProducao),
    dataNecessidade: normalizeDate(data.dataNecessidade),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(item: MrpItem) {
  return {
    produto: item.produto.trim() === "" ? null : Number(item.produto),
    periodo: item.periodo.trim() || null,
    demandaPrevista: toNullableNumber(item.demandaPrevista),
    estoqueAtual: toNullableNumber(item.estoqueAtual),
    estoqueSeguranca: toNullableNumber(item.estoqueSeguranca),
    dataNecessidade: item.dataNecessidade.trim() || null,
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

export default function MrpPage({ embedded = false }: MrpPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<MrpItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<MrpItem | null>(null);
  const [draft, setDraft] = useState<MrpItem>(emptyMrp);
  const [productOptions, setProductOptions] = useState<ProductOption[]>([]);
  const [productAccess, setProductAccess] = useState<ProductAccess>("idle");
  const canRead = canAccessResourceAction(session, mrpResource, "read");
  const canCreate = canAccessResourceAction(session, mrpResource, "create");
  const canUpdate = canAccessResourceAction(session, mrpResource, "update");
  const canDelete = canAccessResourceAction(session, mrpResource, "delete");
  const canReadProducts = canAccessResourceAction(
    session,
    productsResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadMrpItems() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyMrp });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("pp", "mrp");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeMrp(item as Record<string, unknown>))
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar o MRP."));
    } finally {
      setLoading(false);
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
            .map((item) => mapProductOption(item as Record<string, unknown>))
            .filter((item): item is ProductOption => item !== null)
        : [];
      setProductOptions(nextItems);
      setProductAccess("loaded");
    } catch {
      setProductOptions([]);
      setProductAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadMrpItems();
  }, [canRead]);

  useEffect(() => {
    void loadProducts();
  }, [canReadProducts]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.periodo, item.dataNecessidade, item.produto]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyMrp });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: MrpItem) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: MrpItem) {
    onChangeGuard(next);
  }

  function onChangeGuard(next: MrpItem) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar o MRP."
          : "Seu perfil nao possui permissao para criar o MRP.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("pp", "mrp", selected.id, payload)
        : await createResource("pp", "mrp", payload);

      const saved = normalizeMrp(response.data as Record<string, unknown>);
      await loadMrpItems();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "MRP atualizado com sucesso."
          : "MRP criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o MRP."
            : "Nao foi possivel criar o MRP.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: MrpItem) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir o MRP.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o MRP para exclusao.");
      return;
    }

    const confirmed = window.confirm(`Deseja excluir o registro MRP #${item.id}?`);

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("pp", "mrp", item.id);
      await loadMrpItems();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyMrp });
      }

      setSuccess("MRP excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o MRP."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "mrp-page mrp-page--embedded" : "mrp-page"}>
      {!embedded ? (
        <header className="mrp-page__header">
          <div>
            <span className="mrp-page__eyebrow">PP</span>
            <h2 className="mrp-page__title">MRP</h2>
            <p className="mrp-page__subtitle">
              Planeje demanda, estoque e necessidade de compra ou producao por
              periodo.
            </p>
          </div>

          <div className="mrp-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por produto, periodo ou data de necessidade"
              className="mrp-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="mrp-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo MRP
            </button>
          </div>
        </header>
      ) : (
        <div className="mrp-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por produto, periodo ou data de necessidade"
            className="mrp-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="mrp-page__toolbar-actions">
            <button
              type="button"
              className="mrp-page__ghost"
              onClick={() => void loadMrpItems()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="mrp-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo MRP
            </button>
          </div>
        </div>
      )}

      {error ? <div className="mrp-page__alert">{error}</div> : null}
      {success ? (
        <div className="mrp-page__alert mrp-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="mrp-page__alert mrp-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="mrp-page__layout">
        <MrpTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          productOptions={productOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <MrpForm
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
