import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import EfdRecordsForm from "../../components/Fiscal/EfdRecordsForm";
import EfdRecordsTable from "../../components/Fiscal/EfdRecordsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./efd-records-page.css";

export type EfdRecordEntry = {
  id?: number;
  periodo: string;
  registro: string;
  conteudo: string;
  createdAt?: string;
};

type EfdRecordsPageProps = {
  embedded?: boolean;
};

const emptyEntry: EfdRecordEntry = {
  periodo: "",
  registro: "",
  conteudo: "",
};

const efdRecordsResource = {
  schema: "fiscal",
  entity: "efdRegistros",
  label: "Registros EFD",
  description: "Registros fiscais EFD.",
} as const;

function normalizeEntry(data: Record<string, unknown>): EfdRecordEntry {
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

function toRequestPayload(entry: EfdRecordEntry) {
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

export default function EfdRecordsPage({
  embedded = false,
}: EfdRecordsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<EfdRecordEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<EfdRecordEntry | null>(null);
  const [draft, setDraft] = useState<EfdRecordEntry>(emptyEntry);
  const canRead = canAccessResourceAction(session, efdRecordsResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    efdRecordsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    efdRecordsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    efdRecordsResource,
    "delete",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadRecords = useCallback(async () => {
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
      const response = await listResource("fiscal", "efdRegistros");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeEntry(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível carregar os registros EFD."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadRecords();
  }, [loadRecords]);

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

  function handleSelect(item: EfdRecordEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: EfdRecordEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar registros EFD."
          : "Seu perfil não possui permissão para criar registros EFD.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fiscal", "efdRegistros", selected.id, payload)
        : await createResource("fiscal", "efdRegistros", payload);

      const saved = normalizeEntry(response.data as Record<string, unknown>);
      await loadRecords();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Registro EFD atualizado com sucesso."
          : "Registro EFD criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o registro EFD."
            : "Não foi possível criar o registro EFD.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: EfdRecordEntry) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir registros EFD.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o registro EFD para exclusão.");
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
      await deleteResource("fiscal", "efdRegistros", item.id);
      await loadRecords();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyEntry });
      }

      setSuccess("Registro EFD excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível excluir o registro EFD."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "efd-records-page efd-records-page--embedded"
          : "efd-records-page"
      }
    >
      {!embedded ? (
        <header className="efd-records-page__header">
          <div>
            <span className="efd-records-page__eyebrow">FISCAL</span>
            <h2 className="efd-records-page__title">Registros EFD</h2>
            <p className="efd-records-page__subtitle">
              Gerencie registros EFD com periodo de apuração, codigo estruturado
              e conteudo fiscal em JSON.
            </p>
          </div>

          <div className="efd-records-page__toolbar-actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por registro, periodo ou conteudo"
              className="efd-records-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="efd-records-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo registro
            </button>
          </div>
        </header>
      ) : (
        <div className="efd-records-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por registro, periodo ou conteudo"
            className="efd-records-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="efd-records-page__toolbar-actions">
            <button
              type="button"
              className="efd-records-page__ghost"
              onClick={() => void loadRecords()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="efd-records-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo registro
            </button>
          </div>
        </div>
      )}

      {error ? <div className="efd-records-page__alert">{error}</div> : null}
      {success ? (
        <div className="efd-records-page__alert efd-records-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="efd-records-page__alert efd-records-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="efd-records-page__layout">
        <EfdRecordsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <EfdRecordsForm
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
