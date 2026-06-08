import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import RiskForm from "../../components/Grc/RiskForm";
import RisksTable from "../../components/Grc/RisksTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { User } from "../Sys/UsersPage";
import "./risks-page.css";

export type RiskRecord = {
  id?: number;
  codigo: string;
  titulo: string;
  descricao: string;
  categoria: string;
  probabilidade: string;
  impacto: string;
  nivelRisco: string;
  responsavelId: string;
  planoMitigacao: string;
  createdAt?: string;
};

type RisksPageProps = {
  embedded?: boolean;
};

const risksResource = {
  schema: "grc",
  entity: "riscos",
  label: "Riscos",
  description: "Mapeamento e avaliacao de riscos.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Gestao de usuarios do tenant.",
} as const;

const emptyRisk: RiskRecord = {
  codigo: "",
  titulo: "",
  descricao: "",
  categoria: "",
  probabilidade: "1",
  impacto: "1",
  nivelRisco: "baixo",
  responsavelId: "",
  planoMitigacao: "",
};

function normalizeRisk(data: Record<string, unknown>): RiskRecord {
  const responsavel = data.responsavel as
    | { id?: number | string; nome?: string }
    | null
    | undefined;

  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    titulo: String(data.titulo ?? ""),
    descricao: String(data.descricao ?? ""),
    categoria: String(data.categoria ?? ""),
    probabilidade: data.probabilidade == null ? "1" : String(data.probabilidade),
    impacto: data.impacto == null ? "1" : String(data.impacto),
    nivelRisco: String(data.nivelRisco ?? "baixo"),
    responsavelId:
      responsavel?.id == null ? "" : String(responsavel.id),
    planoMitigacao: String(data.planoMitigacao ?? ""),
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
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function calculateRiskLevel(probabilidade: string, impacto: string) {
  const p = Number(probabilidade);
  const i = Number(impacto);

  if (!Number.isFinite(p) || !Number.isFinite(i)) {
    return "";
  }

  const score = p * i;

  if (score <= 5) {
    return "baixo";
  }

  if (score <= 14) {
    return "medio";
  }

  return "alto";
}

function toRequestPayload(item: RiskRecord) {
  const probabilidade = Number(item.probabilidade);
  const impacto = Number(item.impacto);

  return {
    codigo: item.codigo.trim() || null,
    titulo: item.titulo.trim() || null,
    descricao: item.descricao.trim() || null,
    categoria: item.categoria.trim() || null,
    probabilidade: Number.isFinite(probabilidade) ? probabilidade : null,
    impacto: Number.isFinite(impacto) ? impacto : null,
    responsavel:
      item.responsavelId.trim() === "" ? null : Number(item.responsavelId),
    planoMitigacao: item.planoMitigacao.trim() || null,
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

export default function RisksPage({ embedded = false }: RisksPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<RiskRecord[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<RiskRecord | null>(null);
  const [draft, setDraft] = useState<RiskRecord>(emptyRisk);
  const canRead = canAccessResourceAction(session, risksResource, "read");
  const canCreate = canAccessResourceAction(session, risksResource, "create");
  const canUpdate = canAccessResourceAction(session, risksResource, "update");
  const canDelete = canAccessResourceAction(session, risksResource, "delete");
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadRisks() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyRisk });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("grc", "riscos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) => normalizeRisk(item as Record<string, unknown>))
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os riscos."));
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
    void loadRisks();
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
      [item.codigo, item.titulo, item.categoria, item.nivelRisco]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyRisk });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: RiskRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: RiskRecord) {
    setDraft({
      ...next,
      nivelRisco: calculateRiskLevel(next.probabilidade, next.impacto),
    });
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar riscos."
          : "Seu perfil nao possui permissao para criar riscos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("grc", "riscos", selected.id, payload)
        : await createResource("grc", "riscos", payload);

      const saved = normalizeRisk(response.data as Record<string, unknown>);
      await loadRisks();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Risco atualizado com sucesso."
          : "Risco criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o risco."
            : "Nao foi possivel criar o risco.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: RiskRecord) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir riscos.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o risco para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o risco "${item.titulo || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("grc", "riscos", item.id);
      await loadRisks();
      setSuccess("Risco excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o risco."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className={embedded ? "risks-page risks-page--embedded" : "risks-page"}>
      {!embedded ? (
        <header className="risks-page__header">
          <div>
            <span className="risks-page__eyebrow">GRC</span>
            <h2 className="risks-page__title">Riscos</h2>
            <p className="risks-page__subtitle">
              Estruture riscos, pontue probabilidade e impacto e acompanhe o
              nivel calculado com plano de mitigacao.
            </p>
          </div>

          <div className="risks-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por codigo, titulo ou nivel"
              className="risks-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="risks-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo risco
            </button>
          </div>
        </header>
      ) : (
        <div className="risks-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por codigo, titulo ou nivel"
            className="risks-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="risks-page__toolbar-actions">
            <button
              type="button"
              className="risks-page__ghost"
              onClick={() => void loadRisks()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="risks-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo risco
            </button>
          </div>
        </div>
      )}

      {error ? <div className="risks-page__alert">{error}</div> : null}
      {success ? (
        <div className="risks-page__alert risks-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="risks-page__alert risks-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="risks-page__layout">
        <RisksTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <RiskForm
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
