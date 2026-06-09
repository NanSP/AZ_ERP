import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ConsentForm from "../../components/Grc/ConsentForm";
import ConsentsTable from "../../components/Grc/ConsentsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./consents-page.css";

export type ConsentRecord = {
  id?: number;
  titular: string;
  tipoTitular: string;
  finalidade: string;
  dataConsentimento: string;
  dataRevogacao: string;
  ipAddress: string;
  userAgent: string;
};

type ConsentsPageProps = {
  embedded?: boolean;
};

const consentsResource = {
  schema: "grc",
  entity: "consentimentos",
  label: "Consentimentos",
  description: "Consentimentos e bases legais.",
} as const;

const emptyConsent: ConsentRecord = {
  titular: "",
  tipoTitular: "cliente",
  finalidade: "",
  dataConsentimento: "",
  dataRevogacao: "",
  ipAddress: "",
  userAgent: "",
};

function normalizeDateTimeLocal(value: unknown) {
  if (typeof value !== "string" || value.trim() === "") {
    return "";
  }

  const normalized = value.trim();
  return normalized.length >= 16 ? normalized.slice(0, 16) : normalized;
}

function normalizeConsent(data: Record<string, unknown>): ConsentRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    titular: data.titular == null ? "" : String(data.titular),
    tipoTitular: String(data.tipoTitular ?? "cliente"),
    finalidade: String(data.finalidade ?? ""),
    dataConsentimento: normalizeDateTimeLocal(data.dataConsentimento),
    dataRevogacao: normalizeDateTimeLocal(data.dataRevogacao),
    ipAddress: String(data.ipAddress ?? ""),
    userAgent: String(data.userAgent ?? ""),
  };
}

function toRequestPayload(item: ConsentRecord) {
  return {
    titular: item.titular.trim() === "" ? null : Number(item.titular),
    tipoTitular: item.tipoTitular.trim() || null,
    finalidade: item.finalidade.trim() || null,
    dataConsentimento: item.dataConsentimento.trim() || null,
    dataRevogacao: item.dataRevogacao.trim() || null,
    ipAddress: item.ipAddress.trim() || null,
    userAgent: item.userAgent.trim() || null,
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

export default function ConsentsPage({ embedded = false }: ConsentsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<ConsentRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<ConsentRecord | null>(null);
  const [draft, setDraft] = useState<ConsentRecord>(emptyConsent);
  const canRead = canAccessResourceAction(session, consentsResource, "read");
  const canCreate = canAccessResourceAction(
    session,
    consentsResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    consentsResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    consentsResource,
    "delete",
  );
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadConsents = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyConsent });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("grc", "consentimentos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeConsent(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível carregar os consentimentos."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadConsents();
  }, [loadConsents]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.titular, item.tipoTitular, item.finalidade, item.userAgent]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyConsent });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: ConsentRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: ConsentRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar consentimentos."
          : "Seu perfil não possui permissão para criar consentimentos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("grc", "consentimentos", selected.id, payload)
        : await createResource("grc", "consentimentos", payload);

      const saved = normalizeConsent(response.data as Record<string, unknown>);
      await loadConsents();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Consentimento atualizado com sucesso."
          : "Consentimento criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar o consentimento."
            : "Não foi possível criar o consentimento.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: ConsentRecord) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir consentimentos.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar o consentimento para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o consentimento "${item.finalidade || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("grc", "consentimentos", item.id);
      await loadConsents();
      setSuccess("Consentimento excluído com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível excluir o consentimento."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded ? "consents-page consents-page--embedded" : "consents-page"
      }
    >
      {!embedded ? (
        <header className="consents-page__header">
          <div>
            <span className="consents-page__eyebrow">GRC</span>
            <h2 className="consents-page__title">Consentimentos</h2>
            <p className="consents-page__subtitle">
              Registre base de consentimento, titular, rastreabilidade e
              revogação com foco em governanca e LGPD.
            </p>
          </div>

          <div className="consents-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por titular, tipo, finalidade ou user agent"
              className="consents-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="consents-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo consentimento
            </button>
          </div>
        </header>
      ) : (
        <div className="consents-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por titular, tipo, finalidade ou user agent"
            className="consents-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="consents-page__toolbar-actions">
            <button
              type="button"
              className="consents-page__ghost"
              onClick={() => void loadConsents()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="consents-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo consentimento
            </button>
          </div>
        </div>
      )}

      {error ? <div className="consents-page__alert">{error}</div> : null}
      {success ? (
        <div className="consents-page__alert consents-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="consents-page__alert consents-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="consents-page__layout">
        <ConsentsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ConsentForm
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
