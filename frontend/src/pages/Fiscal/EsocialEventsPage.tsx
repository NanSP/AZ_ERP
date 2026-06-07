import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import EsocialEventsForm from "../../components/Fiscal/EsocialEventsForm";
import EsocialEventsTable from "../../components/Fiscal/EsocialEventsTable";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./esocial-events-page.css";

export type EsocialEventEntry = {
  id?: number;
  periodoApuracao: string;
  tipoEvento: string;
  eventoId: string;
  conteudo: string;
  status: string;
  createdAt?: string;
};

type EsocialEventsPageProps = {
  embedded?: boolean;
};

const emptyEntry: EsocialEventEntry = {
  periodoApuracao: "",
  tipoEvento: "",
  eventoId: "",
  conteudo: "",
  status: "gerado",
};

function normalizeEntry(data: Record<string, unknown>): EsocialEventEntry {
  return {
    id:
      typeof data.id === "number"
        ? data.id
        : typeof data.id === "bigint"
          ? Number(data.id)
          : undefined,
    periodoApuracao: String(data.periodoApuracao ?? ""),
    tipoEvento: String(data.tipoEvento ?? ""),
    eventoId: String(data.eventoId ?? ""),
    conteudo: String(data.conteudo ?? ""),
    status: String(data.status ?? "gerado"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(entry: EsocialEventEntry) {
  return {
    periodoApuracao: entry.periodoApuracao.trim() || null,
    tipoEvento: entry.tipoEvento.trim() || null,
    eventoId: entry.eventoId.trim() || null,
    conteudo: entry.conteudo.trim() || null,
    status: entry.status.trim() || null,
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

export default function EsocialEventsPage({
  embedded = false,
}: EsocialEventsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<EsocialEventEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<EsocialEventEntry | null>(null);
  const [draft, setDraft] = useState<EsocialEventEntry>(emptyEntry);
  const permissionSet = useMemo(
    () => new Set(session?.permissoes ?? []),
    [session?.permissoes],
  );
  const isMasterScope = session?.scope === "master";
  const canRead =
    isMasterScope || permissionSet.has("fiscal:esocial_eventos:read");
  const canCreate =
    isMasterScope || permissionSet.has("fiscal:esocial_eventos:create");
  const canUpdate =
    isMasterScope || permissionSet.has("fiscal:esocial_eventos:update");
  const canDelete =
    isMasterScope || permissionSet.has("fiscal:esocial_eventos:delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadEvents() {
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
      const response = await listResource("fiscal", "esocialEventos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeEntry(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel carregar os eventos eSocial."),
      );
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    void loadEvents();
  }, [canRead]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.tipoEvento, item.eventoId, item.status, item.periodoApuracao]
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

  function handleSelect(item: EsocialEventEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: EsocialEventEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar eventos eSocial."
          : "Seu perfil nao possui permissao para criar eventos eSocial.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("fiscal", "esocialEventos", selected.id, payload)
        : await createResource("fiscal", "esocialEventos", payload);

      const saved = normalizeEntry(response.data as Record<string, unknown>);
      await loadEvents();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Evento eSocial atualizado com sucesso."
          : "Evento eSocial criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o evento eSocial."
            : "Nao foi possivel criar o evento eSocial.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: EsocialEventEntry) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir eventos eSocial.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o evento para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o evento "${item.tipoEvento || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("fiscal", "esocialEventos", item.id);
      await loadEvents();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyEntry });
      }

      setSuccess("Evento eSocial excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Nao foi possivel excluir o evento eSocial."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "esocial-events-page esocial-events-page--embedded"
          : "esocial-events-page"
      }
    >
      {!embedded ? (
        <header className="esocial-events-page__header">
          <div>
            <span className="esocial-events-page__eyebrow">FISCAL</span>
            <h2 className="esocial-events-page__title">Eventos eSocial</h2>
            <p className="esocial-events-page__subtitle">
              Gerencie eventos eSocial com periodo, conteudo estruturado e andamento de envio.
            </p>
          </div>

          <div className="esocial-events-page__toolbar-actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por tipo, evento ID, status ou periodo"
              className="esocial-events-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="esocial-events-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo evento
            </button>
          </div>
        </header>
      ) : (
        <div className="esocial-events-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por tipo, evento ID, status ou periodo"
            className="esocial-events-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="esocial-events-page__toolbar-actions">
            <button
              type="button"
              className="esocial-events-page__ghost"
              onClick={() => void loadEvents()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="esocial-events-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo evento
            </button>
          </div>
        </div>
      )}

      {error ? <div className="esocial-events-page__alert">{error}</div> : null}
      {success ? (
        <div className="esocial-events-page__alert esocial-events-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="esocial-events-page__alert esocial-events-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" · ")}
        </div>
      ) : null}

      <div className="esocial-events-page__layout">
        <EsocialEventsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <EsocialEventsForm
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
