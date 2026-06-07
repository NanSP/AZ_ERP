import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import EcdRecordsForm from "../../components/Fiscal/EcdRecordsForm";
import EcdRecordsTable from "../../components/Fiscal/EcdRecordsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./ecd-records-page.css";

export type EcdRecordEntry = {
  id?: number;
  periodo: string;
  registro: string;
  conteudo: string;
  createdAt?: string;
};

type EcdRecordsPageProps = {
  embedded?: boolean;
};

const emptyEntry: EcdRecordEntry = {
  periodo: "",
  registro: "",
  conteudo: "",
};

const ecdRecordsResource = {
  schema: "fiscal",
  entity: "ecdRegistros",
  label: "Registros ECD",
  description: "Registros contabeis ECD.",
} as const;

function normalizeEntry(data: Record<string, unknown>): EcdRecordEntry {
  return {
    id:
      typeof data.id === "number"
        ? data.id
        : typeof data.id === "bigint"
          ? Number(data.id)
          : undefined,
    periodo: String(data.periodo ?? ""),
    registro: String(data.registro ?? ""),
    conteudo:
      data.conteudo == null ? "" : JSON.stringify(data.conteudo, null, 2),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function parseJsonMap(value: string) {
  const trimmed = value.trim();

  if (!trimmed) {
    return null;
  }

  const parsed = JSON.parse(trimmed) as unknown;

  if (parsed == null || Array.isArray(parsed) || typeof parsed !== "object") {
    throw new Error("JSON precisa ser um objeto.");
  }

  return parsed as Record<string, unknown>;
}

function toRequestPayload(entry: EcdRecordEntry) {
  return {
    periodo: entry.periodo.trim() || null,
    registro: entry.registro.trim() || null,
    conteudo: parseJsonMap(entry.conteudo),
  };
}

function getErrorMessage(error: unknown, fallback: string) {
  if (error instanceof AxiosError) {
    const message = error.response?.data?.message;
    if (typeof message === "string" && message.trim()) {
      return message;
    }
  }

  if (error instanceof Error && error.message.trim()) {
    return error.message;
  }

  return fallback;
}

export default function EcdRecordsPage({
  embedded = false,
}: EcdRecordsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<EcdRecordEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<EcdRecordEntry | null>(null);
  const [draft, setDraft] = useState<EcdRecordEntry>(emptyEntry);
  const canRead = canAccessResourceAction(session, ecdRecordsResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    ecdRecordsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    ecdRecordsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    ecdRecordsResource,
    "delete",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadRecords() {
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
      const response = await listResource("fiscal", "ecdRegistros");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeEntry(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os registros ECD."),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadRecords();
  }, [canRead]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.registro, item.periodo, item.conteudo]
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

  function handleSelect(item: EcdRecordEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: EcdRecordEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar registros ECD."
          : "Seu perfil nao possui permissao para criar registros ECD.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fiscal", "ecdRegistros", selected.id, payload)
        : await createResource("fiscal", "ecdRegistros", payload);

      const saved = normalizeEntry(response.data as Record<string, unknown>);
      await loadRecords();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Registro ECD atualizado com sucesso."
          : "Registro ECD criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o registro ECD."
            : "Nao foi possivel criar o registro ECD.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: EcdRecordEntry) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir registros ECD.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o registro ECD para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o registro "${item.registro || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("fiscal", "ecdRegistros", item.id);
      await loadRecords();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyEntry });
      }

      setSuccess("Registro ECD excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o registro ECD."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "ecd-records-page ecd-records-page--embedded" : "ecd-records-page"
      }
    >
      {!embedded ? (
        <header className="ecd-records-page__header">
          <div>
            <span className="ecd-records-page__eyebrow">FISCAL</span>
            <h2 className="ecd-records-page__title">Registros ECD</h2>
            <p className="ecd-records-page__subtitle">
              Gerencie registros ECD com periodo de apuracao, codigo estruturado e conteudo contabil em JSON.
            </p>
          </div>

          <div className="ecd-records-page__toolbar-actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por registro, periodo ou conteudo"
              className="ecd-records-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="ecd-records-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo registro
            </button>
          </div>
        </header>
      ) : (
        <div className="ecd-records-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por registro, periodo ou conteudo"
            className="ecd-records-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="ecd-records-page__toolbar-actions">
            <button
              type="button"
              className="ecd-records-page__ghost"
              onClick={() => void loadRecords()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="ecd-records-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo registro
            </button>
          </div>
        </div>
      )}

      {error ? <div className="ecd-records-page__alert">{error}</div> : null}
      {success ? (
        <div className="ecd-records-page__alert ecd-records-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="ecd-records-page__alert ecd-records-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="ecd-records-page__layout">
        <EcdRecordsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <EcdRecordsForm
          value={draft}
          editing={!!selected}
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
