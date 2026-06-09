import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import SlaConfigForm from "../../components/Sm/SlaConfigForm";
import SlaConfigsTable from "../../components/Sm/SlaConfigsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./sla-config-page.css";

export type SlaConfigEntry = {
  id?: number;
  tipoServico: string;
  prioridade: string;
  tempoAtendimentoHoras: string;
  tempoResolucaoHoras: string;
  createdAt?: string;
};

type SlaConfigPageProps = {
  embedded?: boolean;
};

const slaResource = {
  schema: "sm",
  entity: "slaConfig",
  label: "SLA",
  description: "Configuração de niveis de serviço.",
} as const;

const emptySlaConfig: SlaConfigEntry = {
  tipoServico: "",
  prioridade: "normal",
  tempoAtendimentoHoras: "",
  tempoResolucaoHoras: "",
};

function normalizeEntry(data: Record<string, unknown>): SlaConfigEntry {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    tipoServico: String(data.tipoServico ?? ""),
    prioridade: String(data.prioridade ?? "normal"),
    tempoAtendimentoHoras:
      data.tempoAtendimentoHoras == null
        ? ""
        : String(data.tempoAtendimentoHoras),
    tempoResolucaoHoras:
      data.tempoResolucaoHoras == null ? "" : String(data.tempoResolucaoHoras),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(entry: SlaConfigEntry) {
  return {
    tipoServico: entry.tipoServico.trim() || null,
    prioridade: entry.prioridade.trim() || null,
    tempoAtendimentoHoras:
      entry.tempoAtendimentoHoras.trim() === ""
        ? null
        : Number(entry.tempoAtendimentoHoras),
    tempoResolucaoHoras:
      entry.tempoResolucaoHoras.trim() === ""
        ? null
        : Number(entry.tempoResolucaoHoras),
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

export default function SlaConfigPage({
  embedded = false,
}: SlaConfigPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<SlaConfigEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<SlaConfigEntry | null>(null);
  const [draft, setDraft] = useState<SlaConfigEntry>(emptySlaConfig);
  const canRead = canAccessResourceAction(session, slaResource, "read");
  const canCreate = canAccessResourceAction(session, slaResource, "create");
  const canUpdate = canAccessResourceAction(session, slaResource, "update");
  const canDelete = canAccessResourceAction(session, slaResource, "delete");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadItems = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptySlaConfig });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sm", "slaConfig");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeEntry(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Não foi possivel carregar as configurações de SLA.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  useEffect(() => {
    void loadItems();
  }, [loadItems]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.tipoServico, item.prioridade]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptySlaConfig });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: SlaConfigEntry) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: SlaConfigEntry) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar configurações de SLA."
          : "Seu perfil não possui permissão para criar configurações de SLA.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sm", "slaConfig", selected.id, payload)
        : await createResource("sm", "slaConfig", payload);

      const saved = normalizeEntry(response.data as Record<string, unknown>);
      await loadItems();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Configuração de SLA atualizada com sucesso."
          : "Configuração de SLA criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possivel atualizar a configuração de SLA."
            : "Não foi possivel criar a configuração de SLA.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: SlaConfigEntry) {
    if (!canDelete) {
      setError(
        "Seu perfil não possui permissão para excluir configurações de SLA.",
      );
      return;
    }

    if (!item.id) {
      setError("Não foi possivel identificar a configuração para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a configuração de SLA "${item.tipoServico} / ${item.prioridade}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sm", "slaConfig", item.id);
      await loadItems();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptySlaConfig });
      }

      setSuccess("Configuração de SLA excluída com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possivel excluir a configuração de SLA."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "sla-config-page sla-config-page--embedded"
          : "sla-config-page"
      }
    >
      {!embedded ? (
        <header className="sla-config-page__header">
          <div>
            <span className="sla-config-page__eyebrow">SM</span>
            <h2 className="sla-config-page__title">SLA</h2>
            <p className="sla-config-page__subtitle">
              Configure prioridade e tempos de atendimento e resolucao por tipo
              de servico.
            </p>
          </div>

          <div className="sla-config-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por tipo de servico ou prioridade"
              className="sla-config-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="sla-config-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova configuracao
            </button>
          </div>
        </header>
      ) : (
        <div className="sla-config-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por tipo de servico ou prioridade"
            className="sla-config-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="sla-config-page__toolbar-actions">
            <button
              type="button"
              className="sla-config-page__ghost"
              onClick={() => void loadItems()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="sla-config-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova configuracao
            </button>
          </div>
        </div>
      )}

      {error ? <div className="sla-config-page__alert">{error}</div> : null}
      {success ? (
        <div className="sla-config-page__alert sla-config-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="sla-config-page__alert sla-config-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="sla-config-page__layout">
        <SlaConfigsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <SlaConfigForm
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
