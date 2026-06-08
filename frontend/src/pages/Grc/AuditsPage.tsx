import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import AuditForm from "../../components/Grc/AuditForm";
import AuditsTable from "../../components/Grc/AuditsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { User } from "../Sys/UsersPage";
import "./audits-page.css";

export type AuditRecord = {
  id?: number;
  titulo: string;
  tipoAuditoria: string;
  escopo: string;
  dataInicio: string;
  dataFim: string;
  responsavelId: string;
  status: string;
  createdAt?: string;
};

type AuditsPageProps = {
  embedded?: boolean;
};

const auditsResource = {
  schema: "grc",
  entity: "auditorias",
  label: "Auditorias",
  description: "Planos e execucao de auditorias.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Gestao de usuarios do tenant.",
} as const;

const emptyAudit: AuditRecord = {
  titulo: "",
  tipoAuditoria: "interna",
  escopo: "",
  dataInicio: "",
  dataFim: "",
  responsavelId: "",
  status: "planejada",
};

function normalizeAudit(data: Record<string, unknown>): AuditRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    titulo: String(data.titulo ?? ""),
    tipoAuditoria: String(data.tipoAuditoria ?? "interna"),
    escopo: String(data.escopo ?? ""),
    dataInicio: data.dataInicio == null ? "" : String(data.dataInicio),
    dataFim: data.dataFim == null ? "" : String(data.dataFim),
    responsavelId: data.responsavel == null ? "" : String(data.responsavel),
    status: String(data.status ?? "planejada"),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizeUser(data: Record<string, unknown>): User {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    nome: String(data.nome ?? ""),
    email: String(data.email ?? ""),
    login: String(data.login ?? ""),
    senha: "",
    documento: String(data.documento ?? ""),
    tipoUsuario: String(data.tipoUsuario ?? "operador"),
    status: String(data.status ?? "ativo"),
    expiracaoSenha: String(data.expiracaoSenha ?? ""),
    tentativasLogin:
      data.tentativasLogin == null ? "0" : String(data.tentativasLogin),
    ultimoAcesso:
      data.ultimoAcesso == null ? undefined : String(data.ultimoAcesso),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toRequestPayload(item: AuditRecord) {
  return {
    titulo: item.titulo.trim() || null,
    tipoAuditoria: item.tipoAuditoria.trim() || null,
    escopo: item.escopo.trim() || null,
    dataInicio: item.dataInicio.trim() || null,
    dataFim: item.dataFim.trim() || null,
    responsavel:
      item.responsavelId.trim() === "" ? null : Number(item.responsavelId),
    status: item.status.trim() || null,
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

export default function AuditsPage({ embedded = false }: AuditsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<AuditRecord[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<AuditRecord | null>(null);
  const [draft, setDraft] = useState<AuditRecord>(emptyAudit);
  const canRead = canAccessResourceAction(session, auditsResource, "read");
  const canCreate = canAccessResourceAction(session, auditsResource, "create");
  const canUpdate = canAccessResourceAction(session, auditsResource, "update");
  const canDelete = canAccessResourceAction(session, auditsResource, "delete");
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadAudits() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyAudit });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("grc", "auditorias");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAudit(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar as auditorias."));
    } finally {
      setLoading(false);
    }
  }

  async function loadUsers() {
    if (!canReadUsers) {
      setUsers([]);
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeUser(item as Record<string, unknown>))
        : [];
      setUsers(nextItems);
    } catch {
      setUsers([]);
    }
  }

  useEffect(() => {
    void loadAudits();
  }, [canRead]);

  useEffect(() => {
    void loadUsers();
  }, [canReadUsers]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.titulo, item.tipoAuditoria, item.status, item.escopo]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyAudit });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: AuditRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: AuditRecord) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar auditorias."
          : "Seu perfil nao possui permissao para criar auditorias.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("grc", "auditorias", selected.id, payload)
        : await createResource("grc", "auditorias", payload);

      const saved = normalizeAudit(response.data as Record<string, unknown>);
      await loadAudits();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Auditoria atualizada com sucesso."
          : "Auditoria criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar a auditoria."
            : "Nao foi possivel criar a auditoria.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: AuditRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir auditorias.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar a auditoria para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a auditoria "${item.titulo || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("grc", "auditorias", item.id);
      await loadAudits();
      setSuccess("Auditoria excluida com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir a auditoria."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "audits-page audits-page--embedded" : "audits-page"}>
      {!embedded ? (
        <header className="audits-page__header">
          <div>
            <span className="audits-page__eyebrow">GRC</span>
            <h2 className="audits-page__title">Auditorias</h2>
            <p className="audits-page__subtitle">
              Planeje auditorias internas, externas e regulatorias com
              periodo, escopo, responsavel e status de execucao.
            </p>
          </div>

          <div className="audits-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por titulo, tipo, status ou escopo"
              className="audits-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="audits-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova auditoria
            </button>
          </div>
        </header>
      ) : (
        <div className="audits-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por titulo, tipo, status ou escopo"
            className="audits-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="audits-page__toolbar-actions">
            <button
              type="button"
              className="audits-page__ghost"
              onClick={() => void loadAudits()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="audits-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova auditoria
            </button>
          </div>
        </div>
      )}

      {error ? <div className="audits-page__alert">{error}</div> : null}
      {success ? (
        <div className="audits-page__alert audits-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="audits-page__alert audits-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="audits-page__layout">
        <AuditsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <AuditForm
          value={draft}
          editing={!!selected}
          users={users}
          canReadUsers={canReadUsers}
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
