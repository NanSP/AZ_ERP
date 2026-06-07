import { useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import AllocatedResourceForm from "../../components/Ps/AllocatedResourceForm";
import AllocatedResourcesTable from "../../components/Ps/AllocatedResourcesTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./allocated-resources-page.css";

export type ProjectOption = {
  id: number;
  label: string;
};

export type ProjectAccess = "idle" | "loaded" | "unavailable";

export type TaskOption = {
  id: number;
  label: string;
  projectId: number | null;
};

export type TaskAccess = "idle" | "loaded" | "unavailable";

export type AllocatedResource = {
  id?: number;
  projeto: string;
  tarefa: string;
  tipoRecurso: string;
  recursoId: string;
  quantidade: string;
  valorUnitario: string;
  valorTotal: string;
  dataAlocacao: string;
  createdAt?: string;
};

type AllocatedResourcesPageProps = {
  embedded?: boolean;
};

const allocatedResourcesResource = {
  schema: "ps",
  entity: "recursosAlocados",
  label: "Recursos Alocados",
  description: "Alocacao de recursos e capacidade.",
} as const;

const projectsResource = {
  schema: "ps",
  entity: "projetos",
  label: "Projetos",
  description: "Projetos e servicos.",
} as const;

const tasksResource = {
  schema: "ps",
  entity: "tarefas",
  label: "Tarefas",
  description: "Planejamento e acompanhamento de tarefas.",
} as const;

const emptyAllocatedResource: AllocatedResource = {
  projeto: "",
  tarefa: "",
  tipoRecurso: "humano",
  recursoId: "",
  quantidade: "",
  valorUnitario: "",
  valorTotal: "",
  dataAlocacao: "",
};

function normalizeAllocatedResource(
  data: Record<string, unknown>,
): AllocatedResource {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    projeto: data.projeto == null ? "" : String(data.projeto),
    tarefa: data.tarefa == null ? "" : String(data.tarefa),
    tipoRecurso: String(data.tipoRecurso ?? "humano"),
    recursoId: data.recursoId == null ? "" : String(data.recursoId),
    quantidade: data.quantidade == null ? "" : String(data.quantidade),
    valorUnitario:
      data.valorUnitario == null ? "" : String(data.valorUnitario),
    valorTotal: data.valorTotal == null ? "" : String(data.valorTotal),
    dataAlocacao: String(data.dataAlocacao ?? ""),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(resource: AllocatedResource) {
  return {
    projeto: resource.projeto.trim() === "" ? null : Number(resource.projeto),
    tarefa: resource.tarefa.trim() === "" ? null : Number(resource.tarefa),
    tipoRecurso: resource.tipoRecurso.trim() || null,
    recursoId:
      resource.recursoId.trim() === "" ? null : Number(resource.recursoId),
    quantidade: toNullableNumber(resource.quantidade),
    valorUnitario: toNullableNumber(resource.valorUnitario),
    dataAlocacao: resource.dataAlocacao.trim() || null,
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

function normalizeProjectOption(data: Record<string, unknown>): ProjectOption | null {
  if (typeof data.id !== "number") {
    return null;
  }

  return {
    id: data.id,
    label: `${String(data.nome ?? "Projeto")} (#${String(data.id)})`,
  };
}

function normalizeTaskOption(data: Record<string, unknown>): TaskOption | null {
  if (typeof data.id !== "number") {
    return null;
  }

  return {
    id: data.id,
    label: `${String(data.titulo ?? "Tarefa")} (#${String(data.id)})`,
    projectId: typeof data.projeto === "number" ? data.projeto : null,
  };
}

export default function AllocatedResourcesPage({
  embedded = false,
}: AllocatedResourcesPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<AllocatedResource[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<AllocatedResource | null>(null);
  const [draft, setDraft] = useState<AllocatedResource>(emptyAllocatedResource);
  const [projectOptions, setProjectOptions] = useState<ProjectOption[]>([]);
  const [projectAccess, setProjectAccess] = useState<ProjectAccess>("idle");
  const [taskOptions, setTaskOptions] = useState<TaskOption[]>([]);
  const [taskAccess, setTaskAccess] = useState<TaskAccess>("idle");
  const canRead = canAccessResourceAction(
    session,
    allocatedResourcesResource,
    "read",
  );
  const canCreate = canAccessResourceAction(
    session,
    allocatedResourcesResource,
    "create",
  );
  const canUpdate = canAccessResourceAction(
    session,
    allocatedResourcesResource,
    "update",
  );
  const canDelete = canAccessResourceAction(
    session,
    allocatedResourcesResource,
    "delete",
  );
  const canReadProjects = canAccessResourceAction(
    session,
    projectsResource,
    "read",
  );
  const canReadTasks = canAccessResourceAction(session, tasksResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  async function loadAllocatedResources() {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyAllocatedResource });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("ps", "recursosAlocados");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeAllocatedResource(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel carregar os recursos alocados.",
        ),
      );
    } finally {
      setLoading(false);
    }
  }

  async function loadProjects() {
    if (!canReadProjects) {
      setProjectOptions([]);
      setProjectAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("ps", "projetos");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => normalizeProjectOption(item as Record<string, unknown>))
            .filter((item): item is ProjectOption => item !== null)
        : [];
      setProjectOptions(nextItems);
      setProjectAccess("loaded");
    } catch {
      setProjectOptions([]);
      setProjectAccess("unavailable");
    }
  }

  async function loadTasks() {
    if (!canReadTasks) {
      setTaskOptions([]);
      setTaskAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("ps", "tarefas");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => normalizeTaskOption(item as Record<string, unknown>))
            .filter((item): item is TaskOption => item !== null)
        : [];
      setTaskOptions(nextItems);
      setTaskAccess("loaded");
    } catch {
      setTaskOptions([]);
      setTaskAccess("unavailable");
    }
  }

  useEffect(() => {
    void loadAllocatedResources();
  }, [canRead]);

  useEffect(() => {
    void loadProjects();
  }, [canReadProjects]);

  useEffect(() => {
    void loadTasks();
  }, [canReadTasks]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.tipoRecurso, item.recursoId, item.quantidade, item.valorTotal]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  const filteredTaskOptions = useMemo(() => {
    const currentProjectId = draft.projeto.trim();

    if (!currentProjectId) {
      return taskOptions;
    }

    return taskOptions.filter(
      (option) => String(option.projectId ?? "") === currentProjectId,
    );
  }, [draft.projeto, taskOptions]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyAllocatedResource });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: AllocatedResource) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: AllocatedResource) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil nao possui permissao para atualizar recursos alocados."
          : "Seu perfil nao possui permissao para criar recursos alocados.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource(
            "ps",
            "recursosAlocados",
            selected.id,
            payload,
          )
        : await createResource("ps", "recursosAlocados", payload);

      const saved = normalizeAllocatedResource(
        response.data as Record<string, unknown>,
      );
      await loadAllocatedResources();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Recurso alocado atualizado com sucesso."
          : "Recurso alocado criado com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Nao foi possivel atualizar o recurso alocado."
            : "Nao foi possivel criar o recurso alocado.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: AllocatedResource) {
    if (!canDelete) {
      setError(
        "Seu perfil nao possui permissao para excluir recursos alocados.",
      );
      return;
    }

    if (!item.id) {
      setError(
        "Nao foi possivel identificar o recurso alocado para exclusao.",
      );
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir o recurso "${item.tipoRecurso || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("ps", "recursosAlocados", item.id);
      await loadAllocatedResources();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyAllocatedResource });
      }

      setSuccess("Recurso alocado excluido com sucesso.");
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          "Nao foi possivel excluir o recurso alocado.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={
        embedded
          ? "allocated-resources-page allocated-resources-page--embedded"
          : "allocated-resources-page"
      }
    >
      {!embedded ? (
        <header className="allocated-resources-page__header">
          <div>
            <span className="allocated-resources-page__eyebrow">PS</span>
            <h2 className="allocated-resources-page__title">
              Recursos Alocados
            </h2>
            <p className="allocated-resources-page__subtitle">
              Gerencie alocacao de recursos humanos, materiais e financeiros em projetos e tarefas.
            </p>
          </div>

          <div className="allocated-resources-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por tipo, recurso, quantidade ou valor"
              className="allocated-resources-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="allocated-resources-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo recurso
            </button>
          </div>
        </header>
      ) : (
        <div className="allocated-resources-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por tipo, recurso, quantidade ou valor"
            className="allocated-resources-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="allocated-resources-page__toolbar-actions">
            <button
              type="button"
              className="allocated-resources-page__ghost"
              onClick={() => void loadAllocatedResources()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="allocated-resources-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Novo recurso
            </button>
          </div>
        </div>
      )}

      {error ? (
        <div className="allocated-resources-page__alert">{error}</div>
      ) : null}
      {success ? (
        <div className="allocated-resources-page__alert allocated-resources-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="allocated-resources-page__alert allocated-resources-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criacao desabilitada",
            canDelete ? null : "exclusao desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="allocated-resources-page__layout">
        <AllocatedResourcesTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          projectOptions={projectOptions}
          taskOptions={taskOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <AllocatedResourceForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          projectOptions={projectOptions}
          projectAccess={projectAccess}
          taskOptions={filteredTaskOptions}
          taskAccess={taskAccess}
          onChange={handleChange}
          onSave={() => void handleSave()}
          onReset={handleCreateNew}
        />
      </div>
    </div>
  );
}
