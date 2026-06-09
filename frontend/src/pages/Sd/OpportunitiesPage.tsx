import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import OpportunityForm from "../../components/Sd/OpportunityForm";
import OpportunitiesTable from "../../components/Sd/OpportunitiesTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import type { ClientRecord } from "./ClientsPage";
import type { User } from "../Sys/UsersPage";
import "./opportunities-page.css";

export type OpportunityRecord = {
  id?: number;
  clienteId: string;
  titulo: string;
  descricao: string;
  valorEstimado: string;
  probabilidade: string;
  estagio: string;
  dataPrevistaFechamento: string;
  motivoPerda: string;
  responsavelId: string;
  createdAt?: string;
};

type OpportunitiesPageProps = {
  embedded?: boolean;
};

const opportunitiesResource = {
  schema: "sd",
  entity: "oportunidades",
  label: "Oportunidades",
  description: "Pipeline comercial e oportunidades.",
} as const;

const clientsResource = {
  schema: "sd",
  entity: "clientes",
  label: "Clientes",
  description: "Base comercial de clientes.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Gestao de usuarios do tenant.",
} as const;

const emptyOpportunity: OpportunityRecord = {
  clienteId: "",
  titulo: "",
  descricao: "",
  valorEstimado: "",
  probabilidade: "50",
  estagio: "prospeccao",
  dataPrevistaFechamento: "",
  motivoPerda: "",
  responsavelId: "",
};

function normalizeOpportunity(
  data: Record<string, unknown>,
): OpportunityRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    clienteId: data.cliente == null ? "" : String(data.cliente),
    titulo: String(data.titulo ?? ""),
    descricao: String(data.descricao ?? ""),
    valorEstimado: data.valorEstimado == null ? "" : String(data.valorEstimado),
    probabilidade:
      data.probabilidade == null ? "50" : String(data.probabilidade),
    estagio: String(data.estagio ?? "prospeccao"),
    dataPrevistaFechamento:
      data.dataPrevistaFechamento == null
        ? ""
        : String(data.dataPrevistaFechamento),
    motivoPerda: String(data.motivoPerda ?? ""),
    responsavelId: data.responsavel == null ? "" : String(data.responsavel),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function normalizeClient(data: Record<string, unknown>): ClientRecord {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    parceiroId: data.parceiro == null ? "" : String(data.parceiro),
    classificacao: String(data.classificacao ?? "lead"),
    origem: String(data.origem ?? ""),
    website: String(data.website ?? ""),
    faturamentoAnual:
      data.faturamentoAnual == null ? "" : String(data.faturamentoAnual),
    numeroFuncionarios:
      data.numeroFuncionarios == null ? "" : String(data.numeroFuncionarios),
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

function toRequestPayload(item: OpportunityRecord) {
  function toNumberOrNull(value: string) {
    const normalized = value.trim();

    if (normalized === "") {
      return null;
    }

    return Number(normalized.replace(",", "."));
  }

  return {
    cliente: item.clienteId.trim() === "" ? null : Number(item.clienteId),
    titulo: item.titulo.trim() || null,
    descricao: item.descricao.trim() || null,
    valorEstimado: toNumberOrNull(item.valorEstimado),
    probabilidade:
      item.probabilidade.trim() === "" ? null : Number(item.probabilidade),
    estagio: item.estagio.trim() || null,
    dataPrevistaFechamento: item.dataPrevistaFechamento.trim() || null,
    motivoPerda: item.motivoPerda.trim() || null,
    responsavel:
      item.responsavelId.trim() === "" ? null : Number(item.responsavelId),
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

export default function OpportunitiesPage({
  embedded = false,
}: OpportunitiesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<OpportunityRecord[]>([]);
  const [clients, setClients] = useState<ClientRecord[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<OpportunityRecord | null>(null);
  const [draft, setDraft] = useState<OpportunityRecord>(emptyOpportunity);
  const canRead = canAccessResourceAction(
    session,
    opportunitiesResource,
    "read",
  );
  const canCreate = canAccessResourceAction(
    session,
    opportunitiesResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    opportunitiesResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    opportunitiesResource,
    "delete",
  );
  const canReadClients = canAccessResourceAction(
    session,
    clientsResource,
    "read",
  );
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadOpportunities = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyOpportunity });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("sd", "oportunidades");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeOpportunity(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível carregar as oportunidades."),
      );
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadClients = useCallback(async () => {
    if (!canReadClients) {
      setClients([]);
      return;
    }

    try {
      const response = await listResource("sd", "clientes");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeClient(item as Record<string, unknown>),
          )
        : [];
      setClients(nextItems);
    } catch {
      setClients([]);
    }
  }, [canReadClients]);

  const loadUsers = useCallback(async () => {
    if (!canReadUsers) {
      setUsers([]);
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeUser(item as Record<string, unknown>),
          )
        : [];
      setUsers(nextItems);
    } catch {
      setUsers([]);
    }
  }, [canReadUsers]);

  useEffect(() => {
    void loadOpportunities();
  }, [loadOpportunities]);

  useEffect(() => {
    void loadClients();
  }, [loadClients]);

  useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  const clientOptions = useMemo(() => clients, [clients]);
  const userOptions = useMemo(
    () => users.filter((user) => user.status === "ativo"),
    [users],
  );

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [
        item.titulo,
        item.estagio,
        item.descricao,
        item.valorEstimado,
        item.probabilidade,
        item.clienteId,
        item.responsavelId,
      ]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyOpportunity });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: OpportunityRecord) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: OpportunityRecord) {
    const nextDraft = { ...next };

    if (nextDraft.estagio !== "fechado_perdido") {
      nextDraft.motivoPerda = "";
    }

    if (
      nextDraft.estagio === "prospeccao" ||
      nextDraft.estagio === "qualificacao"
    ) {
      nextDraft.dataPrevistaFechamento = "";
    }

    setDraft(nextDraft);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar oportunidades."
          : "Seu perfil não possui permissão para criar oportunidades.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("sd", "oportunidades", selected.id, payload)
        : await createResource("sd", "oportunidades", payload);

      const saved = normalizeOpportunity(
        response.data as Record<string, unknown>,
      );
      await loadOpportunities();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Oportunidade atualizada com sucesso."
          : "Oportunidade criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar a oportunidade."
            : "Não foi possível criar a oportunidade.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: OpportunityRecord) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir oportunidades.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar a oportunidade para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a oportunidade "${item.titulo || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("sd", "oportunidades", item.id);
      await loadOpportunities();
      setSuccess("Oportunidade excluída com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(err, "Não foi possível excluir a oportunidade."),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "opportunities-page opportunities-page--embedded"
          : "opportunities-page"
      }
    >
      {!embedded ? (
        <header className="opportunities-page__header">
          <div>
            <span className="opportunities-page__eyebrow">SD</span>
            <h2 className="opportunities-page__title">Oportunidades</h2>
            <p className="opportunities-page__subtitle">
              Gerencie o pipeline comercial com clientes, valores estimados,
              probabilidade de fechamento e responsabilidade operacional.
            </p>
          </div>

          <div className="opportunities-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por titulo, estagio, cliente, responsavel ou valor"
              className="opportunities-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="opportunities-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova oportunidade
            </button>
          </div>
        </header>
      ) : (
        <div className="opportunities-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por titulo, estagio, cliente, responsavel ou valor"
            className="opportunities-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="opportunities-page__toolbar-actions">
            <button
              type="button"
              className="opportunities-page__ghost"
              onClick={() => void loadOpportunities()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="opportunities-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova oportunidade
            </button>
          </div>
        </div>
      )}

      {error ? <div className="opportunities-page__alert">{error}</div> : null}
      {success ? (
        <div className="opportunities-page__alert opportunities-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="opportunities-page__alert opportunities-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="opportunities-page__layout">
        <OpportunitiesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <OpportunityForm
          value={draft}
          editing={!!selected}
          clients={clientOptions}
          users={userOptions}
          canReadClients={canReadClients}
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
