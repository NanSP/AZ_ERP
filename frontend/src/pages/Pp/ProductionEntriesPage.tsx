import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ProductionEntryForm from "../../components/Pp/ProductionEntryForm";
import ProductionEntriesTable from "../../components/Pp/ProductionEntriesTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./production-entries-page.css";

export type ProductionOrderOption = {
  id: number;
  label: string;
};

export type EmployeeOption = {
  id: number;
  label: string;
};

export type RelatedAccess = "idle" | "loaded" | "unavailable";

export type ProductionEntry = {
  id?: number;
  op: string;
  maquinaId: string;
  operador: string;
  dataHoraInicio: string;
  dataHoraFim: string;
  quantidadeProduzida: string;
  quantidadeRefugo: string;
  tempoParado: string;
  observacoes: string;
  createdAt?: string;
};

type ProductionEntriesPageProps = {
  embedded?: boolean;
};

const entriesResource = {
  schema: "pp",
  entity: "apontamentos",
  label: "Apontamentos",
  description: "Apontamentos de producao e operacao.",
} as const;

const ordersResource = {
  schema: "pp",
  entity: "ordemProducao",
  label: "Ordens de Producao",
  description: "Ordens e execucao de producao.",
} as const;

const employeesResource = {
  schema: "rh",
  entity: "colaboradores",
  label: "Colaboradores",
  description: "Operadores e equipe.",
} as const;

const emptyEntry: ProductionEntry = {
  op: "",
  maquinaId: "",
  operador: "",
  dataHoraInicio: "",
  dataHoraFim: "",
  quantidadeProduzida: "",
  quantidadeRefugo: "",
  tempoParado: "",
  observacoes: "",
};

function normalizeDateTime(value: unknown) {
  if (value == null) {
    return "";
  }

  const raw = String(value);
  return raw.length >= 16 ? raw.slice(0, 16) : raw;
}

function normalizeEntry(data: Record<string, unknown>): ProductionEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    op: data.op == null ? "" : String(data.op),
    maquinaId: data.maquinaId == null ? "" : String(data.maquinaId),
    operador: data.operador == null ? "" : String(data.operador),
    dataHoraInicio: normalizeDateTime(data.dataHoraInicio),
    dataHoraFim: normalizeDateTime(data.dataHoraFim),
    quantidadeProduzida:
      data.quantidadeProduzida == null ? "" : String(data.quantidadeProduzida),
    quantidadeRefugo:
      data.quantidadeRefugo == null ? "" : String(data.quantidadeRefugo),
    tempoParado: data.tempoParado == null ? "" : String(data.tempoParado),
    observacoes: String(data.observacoes ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toNullableInteger(value: string) {
  return value.trim() === "" ? null : Number(value);
}

function toRequestPayload(entry: ProductionEntry) {
  return {
    op: entry.op.trim() === "" ? null : Number(entry.op),
    maquinaId: toNullableInteger(entry.maquinaId),
    operador: entry.operador.trim() === "" ? null : Number(entry.operador),
    dataHoraInicio: entry.dataHoraInicio.trim() || null,
    dataHoraFim: entry.dataHoraFim.trim() || null,
    quantidadeProduzida: toNullableNumber(entry.quantidadeProduzida),
    quantidadeRefugo: toNullableNumber(entry.quantidadeRefugo),
    tempoParado: toNullableNumber(entry.tempoParado),
    observacoes: entry.observacoes.trim() || null,
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

function mapOption(
  item: Record<string, unknown>,
  fallbackLabel: string,
  preferredField?: string,
) {
  if (typeof item.id !== "number") {
    return null;
  }

  const preferred =
    preferredField && preferredField in item ? item[preferredField] : null;

  return {
    id: item.id,
    label: `${String(preferred ?? item.nome ?? item.codigo ?? fallbackLabel)} (#${String(item.id)})`,
  };
}

export default function ProductionEntriesPage({
  embedded = false,
}: ProductionEntriesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ProductionEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<ProductionEntry | null>(null);
  const [draft, setDraft] = useState<ProductionEntry>(emptyEntry);
  const [orderOptions, setOrderOptions] = useState<ProductionOrderOption[]>([]);
  const [orderAccess, setOrderAccess] = useState<RelatedAccess>("idle");
  const [employeeOptions, setEmployeeOptions] = useState<EmployeeOption[]>([]);
  const [employeeAccess, setEmployeeAccess] = useState<RelatedAccess>("idle");
  const canRead = canAccessResourceAction(session, entriesResource, "read");
  const canCreate = canAccessResourceAction(session, entriesResource, "create");
  const canUpdate = canAccessResourceAction(session, entriesResource, "update");
  const canDelete = canAccessResourceAction(session, entriesResource, "delete");
  const canReadOrders = canAccessResourceAction(session, ordersResource, "read");
  const canReadEmployees = canAccessResourceAction(
    session,
    employeesResource,
    "read",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadEntries() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyEntry });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("pp", "apontamentos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeEntry(item as Record<string, unknown>))
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os apontamentos."),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadOrders() {
    if (!canReadOrders) {
      setOrderOptions([]);
      setOrderAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("pp", "ordemProducao");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) =>
              mapOption(item as Record<string, unknown>, "OP", "numeroOp"),
            )
            .filter((item): item is ProductionOrderOption => item !== null)
        : [];
      setOrderOptions(nextItems);
      setOrderAccess("loaded");
    } catch {
      setOrderOptions([]);
      setOrderAccess("unavailable");
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
            .map((item) =>
              mapOption(item as Record<string, unknown>, "Operador", "nome"),
            )
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
    void loadEntries();
  }, [canRead]);

  useEffect(() => {
    void loadOrders();
  }, [canReadOrders]);

  useEffect(() => {
    void loadEmployees();
  }, [canReadEmployees]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.maquinaId, item.observacoes, item.op]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyEntry });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: ProductionEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ProductionEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar apontamentos."
          : "Seu perfil nao possui permissao para criar apontamentos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("pp", "apontamentos", selected.id, payload)
        : await createResource("pp", "apontamentos", payload);

      const saved = normalizeEntry(response.data as Record<string, unknown>);
      await loadEntries();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Apontamento atualizado com sucesso."
          : "Apontamento criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o apontamento."
            : "Nao foi possivel criar o apontamento.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: ProductionEntry) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir apontamentos.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o apontamento para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o apontamento #${item.id}?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("pp", "apontamentos", item.id);
      await loadEntries();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyEntry });
      }

      setSuccess("Apontamento excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o apontamento."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "production-entries-page production-entries-page--embedded"
          : "production-entries-page"
      }
    >
      {!embedded ? (
        <header className="production-entries-page__header">
          <div>
            <span className="production-entries-page__eyebrow">PP</span>
            <h2 className="production-entries-page__title">Apontamentos</h2>
            <p className="production-entries-page__subtitle">
              Registre inicio, fim, operador, producao, refugo e paradas da
              execucao industrial.
            </p>
          </div>

          <div className="production-entries-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por OP, maquina ou observacoes"
              className="production-entries-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="production-entries-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo apontamento
            </button>
          </div>
        </header>
      ) : (
        <div className="production-entries-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por OP, maquina ou observacoes"
            className="production-entries-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="production-entries-page__toolbar-actions">
            <button
              type="button"
              className="production-entries-page__ghost"
              onClick={() => void loadEntries()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="production-entries-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo apontamento
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="production-entries-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="production-entries-page__alert production-entries-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="production-entries-page__alert production-entries-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="production-entries-page__layout">
        <ProductionEntriesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          orderOptions={orderOptions}
          employeeOptions={employeeOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ProductionEntryForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          orderOptions={orderOptions}
          orderAccess={orderAccess}
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
