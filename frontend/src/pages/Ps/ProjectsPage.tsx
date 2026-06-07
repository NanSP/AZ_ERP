import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import ProjectForm from "../../components/Ps/ProjectForm";
import ProjectsTable from "../../components/Ps/ProjectsTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./projects-page.css";

export type PartnerOption = {
  id: number;
  label: string;
};

export type PartnerAccess = "idle" | "loaded" | "unavailable";

export type UserOption = {
  id: number;
  label: string;
};

export type UserAccess = "idle" | "loaded" | "unavailable";

export type Project = {
  id?: number;
  codigo: string;
  nome: string;
  descricao: string;
  cliente: string;
  gerente: string;
  dataInicio: string;
  dataFim: string;
  dataPrevistaInicio: string;
  dataPrevistaFim: string;
  orcamentoTotal: string;
  orcamentoGasto: string;
  status: string;
  prioridade: string;
  createdAt?: string;
};

type ProjectsPageProps = {
  embedded?: boolean;
};

const projectResource = {
  schema: "ps",
  entity: "projetos",
  label: "Projetos",
  description: "Projetos e servicos.",
} as const;

const partnersResource = {
  schema: "core",
  entity: "parceiros",
  label: "Parceiros",
  description: "Clientes e parceiros.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Usuarios do tenant.",
} as const;

const emptyProject: Project = {
  codigo: "",
  nome: "",
  descricao: "",
  cliente: "",
  gerente: "",
  dataInicio: "",
  dataFim: "",
  dataPrevistaInicio: "",
  dataPrevistaFim: "",
  orcamentoTotal: "",
  orcamentoGasto: "",
  status: "planejado",
  prioridade: "1",
};

function normalizeProject(data: Record<string, unknown>): Project {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    codigo: String(data.codigo ?? ""),
    nome: String(data.nome ?? ""),
    descricao: String(data.descricao ?? ""),
    cliente: data.cliente == null ? "" : String(data.cliente),
    gerente: data.gerente == null ? "" : String(data.gerente),
    dataInicio: String(data.dataInicio ?? ""),
    dataFim: String(data.dataFim ?? ""),
    dataPrevistaInicio: String(data.dataPrevistaInicio ?? ""),
    dataPrevistaFim: String(data.dataPrevistaFim ?? ""),
    orcamentoTotal:
      data.orcamentoTotal == null ? "" : String(data.orcamentoTotal),
    orcamentoGasto:
      data.orcamentoGasto == null ? "" : String(data.orcamentoGasto),
    status: String(data.status ?? "planejado"),
    prioridade: data.prioridade == null ? "1" : String(data.prioridade),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(project: Project) {
  return {
    codigo: project.codigo.trim() || null,
    nome: project.nome.trim() || null,
    descricao: project.descricao.trim() || null,
    cliente: project.cliente.trim() === "" ? null : Number(project.cliente),
    gerente: project.gerente.trim() === "" ? null : Number(project.gerente),
    dataInicio: project.dataInicio.trim() || null,
    dataFim: project.dataFim.trim() || null,
    dataPrevistaInicio: project.dataPrevistaInicio.trim() || null,
    dataPrevistaFim: project.dataPrevistaFim.trim() || null,
    orcamentoTotal: toNullableNumber(project.orcamentoTotal),
    orcamentoGasto: toNullableNumber(project.orcamentoGasto),
    status: project.status.trim() || null,
    prioridade:
      project.prioridade.trim() === "" ? null : Number(project.prioridade),
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

export default function ProjectsPage({ embedded = false }: ProjectsPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Project[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Project | null>(null);
  const [draft, setDraft] = useState<Project>(emptyProject);
  const [partnerOptions, setPartnerOptions] = useState<PartnerOption[]>([]);
  const [partnerAccess, setPartnerAccess] = useState<PartnerAccess>("idle");
  const [userOptions, setUserOptions] = useState<UserOption[]>([]);
  const [userAccess, setUserAccess] = useState<UserAccess>("idle");
  const canRead = canAccessResourceAction(session, projectResource, "read");
  const canCreate = canAccessResourceAction(session, projectResource, "create");
  const canUpdate = canAccessResourceAction(session, projectResource, "update");
  const canDelete = canAccessResourceAction(session, projectResource, "delete");
  const canReadPartners = canAccessResourceAction(
    session,
    partnersResource,
    "read",
  );
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadProjects() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyProject });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("ps", "projetos");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeProject(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel carregar os projetos."));
    } finally {
      setLoading(false);
    }
  }

  async function loadPartners() {
    if (!canReadPartners) {
      setPartnerOptions([]);
      setPartnerAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("core", "parceiros");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? "Parceiro")} (#${String(item.id)})`,
            }))
        : [];
      setPartnerOptions(nextItems);
      setPartnerAccess("loaded");
    } catch {
      setPartnerOptions([]);
      setPartnerAccess("unavailable");
    }
  }

  async function loadUsers() {
    if (!canReadUsers) {
      setUserOptions([]);
      setUserAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => item as Record<string, unknown>)
            .filter((item) => typeof item.id === "number")
            .map((item) => ({
              id: item.id as number,
              label: `${String(item.nome ?? item.login ?? "Usuario")} (#${String(item.id)})`,
            }))
        : [];
      setUserOptions(nextItems);
      setUserAccess("loaded");
    } catch {
      setUserOptions([]);
      setUserAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadProjects();
  }, [canRead]);

  useEffect(() => {
    void loadPartners();
  }, [canReadPartners]);

  useEffect(() => {
    void loadUsers();
  }, [canReadUsers]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.codigo, item.nome, item.status, item.descricao]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyProject });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Project) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Project) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar projetos."
          : "Seu perfil nao possui permissao para criar projetos.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("ps", "projetos", selected.id, payload)
        : await createResource("ps", "projetos", payload);

      const saved = normalizeProject(response.data as Record<string, unknown>);
      await loadProjects();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Projeto atualizado com sucesso."
          : "Projeto criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o projeto."
            : "Nao foi possivel criar o projeto.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Project) {
    if (!canDelete) {
      setError("Seu perfil nao possui permissao para excluir projetos.");
      return;
    }

    if (!item.id) {
      setError("Nao foi possivel identificar o projeto para exclusao.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o projeto "${item.nome || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("ps", "projetos", item.id);
      await loadProjects();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyProject });
      }

      setSuccess("Projeto excluido com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Nao foi possivel excluir o projeto."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={embedded ? "projects-page projects-page--embedded" : "projects-page"}
    >
      {!embedded ? (
        <header className="projects-page__header">
          <div>
            <span className="projects-page__eyebrow">PS</span>
            <h2 className="projects-page__title">Projetos</h2>
            <p className="projects-page__subtitle">
              Gerencie escopo, cronograma, orcamento e responsabilidade dos projetos.
            </p>
          </div>

          <div className="projects-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por codigo, nome, descricao ou status"
              className="projects-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="projects-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo projeto
            </button>
          </div>
        </header>
      ) : (
        <div className="projects-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por codigo, nome, descricao ou status"
            className="projects-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="projects-page__toolbar-actions">
            <button
              type="button"
              className="projects-page__ghost"
              onClick={() => void loadProjects()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="projects-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo projeto
            </button>
          </div>
        </div>
      )}

      {error ? <div className="projects-page__alert">{error}</div> : null}
      {success ? (
        <div className="projects-page__alert projects-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="projects-page__alert projects-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="projects-page__layout">
        <ProjectsTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          partnerOptions={partnerOptions}
          userOptions={userOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <ProjectForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          partnerOptions={partnerOptions}
          partnerAccess={partnerAccess}
          userOptions={userOptions}
          userAccess={userAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
