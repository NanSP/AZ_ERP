import { useCallback, useEffect, useMemo, useState } from "react";
import { AxiosError } from "axios";
import { useAuth } from "../../auth/useAuth";
import TaskForm from "../../components/Ps/TaskForm";
import TasksTable from "../../components/Ps/TasksTable";
import { canAccessResourceAction } from "../../services/accessControl";
import {
  createResource,
  deleteResource,
  listResource,
  updateResource,
} from "../../services/resourceService";
import "./tasks-page.css";

export type ProjectOption = {
  id: number;
  label: string;
};

export type ProjectAccess = "idle" | "loaded" | "unavailable";

export type ParentTaskOption = {
  id: number;
  label: string;
  projectId: number | null;
};

export type ParentTaskAccess = "idle" | "loaded" | "unavailable";

export type UserOption = {
  id: number;
  label: string;
};

export type UserAccess = "idle" | "loaded" | "unavailable";

export type Task = {
  id?: number;
  projeto: string;
  tarefaPai: string;
  titulo: string;
  descricao: string;
  responsavel: string;
  dataInicio: string;
  dataFim: string;
  horasEstimadas: string;
  horasRealizadas: string;
  percentualConcluido: string;
  status: string;
  prioridade: string;
  createdAt?: string;
};

type TasksPageProps = {
  embedded?: boolean;
};

const tasksResource = {
  schema: "ps",
  entity: "tarefas",
  label: "Tarefas",
  description: "Planejamento e acompanhamento de tarefas.",
} as const;

const projectsResource = {
  schema: "ps",
  entity: "projetos",
  label: "Projetos",
  description: "Projetos e servicos.",
} as const;

const usersResource = {
  schema: "sys",
  entity: "usuarios",
  label: "Usuarios",
  description: "Usuarios do tenant.",
} as const;

const emptyTask: Task = {
  projeto: "",
  tarefaPai: "",
  titulo: "",
  descricao: "",
  responsavel: "",
  dataInicio: "",
  dataFim: "",
  horasEstimadas: "",
  horasRealizadas: "",
  percentualConcluido: "0",
  status: "pendente",
  prioridade: "1",
};

function normalizeTask(data: Record<string, unknown>): Task {
  return {
    id: typeof data.id === "number" ? data.id : undefined,
    projeto: data.projeto == null ? "" : String(data.projeto),
    tarefaPai: data.tarefaPai == null ? "" : String(data.tarefaPai),
    titulo: String(data.titulo ?? ""),
    descricao: String(data.descricao ?? ""),
    responsavel: data.responsavel == null ? "" : String(data.responsavel),
    dataInicio: String(data.dataInicio ?? ""),
    dataFim: String(data.dataFim ?? ""),
    horasEstimadas:
      data.horasEstimadas == null ? "" : String(data.horasEstimadas),
    horasRealizadas:
      data.horasRealizadas == null ? "" : String(data.horasRealizadas),
    percentualConcluido:
      data.percentualConcluido == null ? "0" : String(data.percentualConcluido),
    status: String(data.status ?? "pendente"),
    prioridade: data.prioridade == null ? "1" : String(data.prioridade),
    createdAt: data.createdAt == null ? undefined : String(data.createdAt),
  };
}

function toNullableNumber(value: string) {
  return value.trim() === "" ? null : Number(value.replace(",", "."));
}

function toRequestPayload(task: Task) {
  return {
    projeto: task.projeto.trim() === "" ? null : Number(task.projeto),
    tarefaPai: task.tarefaPai.trim() === "" ? null : Number(task.tarefaPai),
    titulo: task.titulo.trim() || null,
    descricao: task.descricao.trim() || null,
    responsavel:
      task.responsavel.trim() === "" ? null : Number(task.responsavel),
    dataInicio: task.dataInicio.trim() || null,
    dataFim: task.dataFim.trim() || null,
    horasEstimadas: toNullableNumber(task.horasEstimadas),
    horasRealizadas: toNullableNumber(task.horasRealizadas),
    percentualConcluido:
      task.percentualConcluido.trim() === ""
        ? null
        : Number(task.percentualConcluido),
    status: task.status.trim() || null,
    prioridade: task.prioridade.trim() === "" ? null : Number(task.prioridade),
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

function normalizeProjectOption(
  data: Record<string, unknown>,
): ProjectOption | null {
  if (typeof data.id !== "number") {
    return null;
  }

  return {
    id: data.id,
    label: `${String(data.nome ?? "Projeto")} (#${String(data.id)})`,
  };
}

function normalizeParentTaskOption(
  data: Record<string, unknown>,
): ParentTaskOption | null {
  if (typeof data.id !== "number") {
    return null;
  }

  return {
    id: data.id,
    label: `${String(data.titulo ?? "Tarefa")} (#${String(data.id)})`,
    projectId: typeof data.projeto === "number" ? data.projeto : null,
  };
}

function normalizeUserOption(data: Record<string, unknown>): UserOption | null {
  if (typeof data.id !== "number") {
    return null;
  }

  return {
    id: data.id,
    label: `${String(data.nome ?? data.login ?? "Usuario")} (#${String(data.id)})`,
  };
}

export default function TasksPage({ embedded = false }: TasksPageProps) {
  const { session } = useAuth();
  const [items, setItems] = useState<Task[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Task | null>(null);
  const [draft, setDraft] = useState<Task>(emptyTask);
  const [projectOptions, setProjectOptions] = useState<ProjectOption[]>([]);
  const [projectAccess, setProjectAccess] = useState<ProjectAccess>("idle");
  const [parentTaskOptions, setParentTaskOptions] = useState<
    ParentTaskOption[]
  >([]);
  const [parentTaskAccess, setParentTaskAccess] =
    useState<ParentTaskAccess>("idle");
  const [userOptions, setUserOptions] = useState<UserOption[]>([]);
  const [userAccess, setUserAccess] = useState<UserAccess>("idle");
  const canRead = canAccessResourceAction(session, tasksResource, "read");
  const canCreate = canAccessResourceAction(session, tasksResource, "create");
  const canUpdate = canAccessResourceAction(session, tasksResource, "update");
  const canDelete = canAccessResourceAction(session, tasksResource, "delete");
  const canReadProjects = canAccessResourceAction(
    session,
    projectsResource,
    "read",
  );
  const canReadUsers = canAccessResourceAction(session, usersResource, "read");
  const canSubmitCurrent = selected ? canUpdate : canCreate;
  const isBusy = loading || saving;

  const loadTasks = useCallback(async () => {
    if (!canRead) {
      setItems([]);
      setSelected(null);
      setDraft({ ...emptyTask });
      setLoading(false);
      setError(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const response = await listResource("ps", "tarefas");
      const nextItems = Array.isArray(response.data)
        ? response.data.map((item) =>
            normalizeTask(item as Record<string, unknown>),
          )
        : [];
      setItems(nextItems);
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível carregar as tarefas."));
    } finally {
      setLoading(false);
    }
  }, [canRead]);

  const loadProjects = useCallback(async () => {
    if (!canReadProjects) {
      setProjectOptions([]);
      setProjectAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("ps", "projetos");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) =>
              normalizeProjectOption(item as Record<string, unknown>),
            )
            .filter((item): item is ProjectOption => item !== null)
        : [];
      setProjectOptions(nextItems);
      setProjectAccess("loaded");
    } catch {
      setProjectOptions([]);
      setProjectAccess("unavailable");
    }
  }, [canReadProjects]);

  const loadParentTasks = useCallback(async () => {
    if (!canRead) {
      setParentTaskOptions([]);
      setParentTaskAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("ps", "tarefas");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) =>
              normalizeParentTaskOption(item as Record<string, unknown>),
            )
            .filter((item): item is ParentTaskOption => item !== null)
        : [];
      setParentTaskOptions(nextItems);
      setParentTaskAccess("loaded");
    } catch {
      setParentTaskOptions([]);
      setParentTaskAccess("unavailable");
    }
  }, [canRead]);

  const loadUsers = useCallback(async () => {
    if (!canReadUsers) {
      setUserOptions([]);
      setUserAccess("unavailable");
      return;
    }

    try {
      const response = await listResource("sys", "usuarios");
      const nextItems = Array.isArray(response.data)
        ? response.data
            .map((item) => normalizeUserOption(item as Record<string, unknown>))
            .filter((item): item is UserOption => item !== null)
        : [];
      setUserOptions(nextItems);
      setUserAccess("loaded");
    } catch {
      setUserOptions([]);
      setUserAccess("unavailable");
    }
  }, [canReadUsers]);

  useEffect(() => {
    void loadTasks();
  }, [loadTasks]);

  useEffect(() => {
    void loadProjects();
  }, [loadProjects]);

  useEffect(() => {
    void loadParentTasks();
  }, [loadParentTasks]);

  useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  const filteredItems = useMemo(() => {
    const normalized = query.trim().toLowerCase();

    if (!normalized) {
      return items;
    }

    return items.filter((item) =>
      [item.titulo, item.descricao, item.status, item.percentualConcluido]
        .filter(Boolean)
        .some((value) => value.toLowerCase().includes(normalized)),
    );
  }, [items, query]);

  const filteredParentTaskOptions = useMemo(() => {
    const currentProjectId = draft.projeto.trim();

    return parentTaskOptions.filter((option) => {
      if (selected?.id != null && option.id === selected.id) {
        return false;
      }

      if (!currentProjectId) {
        return true;
      }

      return String(option.projectId ?? "") === currentProjectId;
    });
  }, [draft.projeto, parentTaskOptions, selected?.id]);

  function handleCreateNew() {
    if (!canCreate) {
      return;
    }

    setSelected(null);
    setDraft({ ...emptyTask });
    setSuccess(null);
    setError(null);
  }

  function handleSelect(item: Task) {
    if (!canUpdate) {
      return;
    }

    setSelected(item);
    setDraft({ ...item });
    setSuccess(null);
    setError(null);
  }

  function handleChange(next: Task) {
    setDraft(next);
  }

  async function handleSave() {
    if (!canSubmitCurrent) {
      setError(
        selected
          ? "Seu perfil não possui permissão para atualizar tarefas."
          : "Seu perfil não possui permissão para criar tarefas.",
      );
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const payload = toRequestPayload(draft);
      const response = selected?.id
        ? await updateResource("ps", "tarefas", selected.id, payload)
        : await createResource("ps", "tarefas", payload);

      const saved = normalizeTask(response.data as Record<string, unknown>);
      await loadTasks();
      await loadParentTasks();
      setSelected(saved);
      setDraft({ ...saved });
      setSuccess(
        selected?.id
          ? "Tarefa atualizada com sucesso."
          : "Tarefa criada com sucesso.",
      );
    } catch (err) {
      setError(
        getErrorMessage(
          err,
          selected?.id
            ? "Não foi possível atualizar a tarefa."
            : "Não foi possível criar a tarefa.",
        ),
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(item: Task) {
    if (!canDelete) {
      setError("Seu perfil não possui permissão para excluir tarefas.");
      return;
    }

    if (!item.id) {
      setError("Não foi possível identificar a tarefa para exclusão.");
      return;
    }

    const confirmed = window.confirm(
      `Deseja excluir a tarefa "${item.titulo || item.id}"?`,
    );

    if (!confirmed) {
      return;
    }

    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      await deleteResource("ps", "tarefas", item.id);
      await loadTasks();
      await loadParentTasks();

      if (selected?.id === item.id) {
        setSelected(null);
        setDraft({ ...emptyTask });
      }

      setSuccess("Tarefa excluida com sucesso.");
    } catch (err) {
      setError(getErrorMessage(err, "Não foi possível excluir a tarefa."));
    } finally {
      setSaving(false);
    }
  }

  return (
    <div
      className={embedded ? "tasks-page tasks-page--embedded" : "tasks-page"}
    >
      {!embedded ? (
        <header className="tasks-page__header">
          <div>
            <span className="tasks-page__eyebrow">PS</span>
            <h2 className="tasks-page__title">Tarefas</h2>
            <p className="tasks-page__subtitle">
              Gerencie tarefas, subtarefas, progresso, responsáveis e horas do
              projeto.
            </p>
          </div>

          <div className="tasks-page__actions">
            <input
              type="search"
              value={query}
              onChange={(event) => setQuery(event.target.value)}
              placeholder="Buscar por titulo, descrição, status ou progresso"
              className="tasks-page__search"
              disabled={isBusy || !canRead}
            />
            <button
              type="button"
              className="tasks-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova tarefa
            </button>
          </div>
        </header>
      ) : (
        <div className="tasks-page__toolbar">
          <input
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Buscar por titulo, descrição, status ou progresso"
            className="tasks-page__search"
            disabled={isBusy || !canRead}
          />
          <div className="tasks-page__toolbar-actions">
            <button
              type="button"
              className="tasks-page__ghost"
              onClick={() => void loadTasks()}
              disabled={isBusy || !canRead}
            >
              Recarregar
            </button>
            <button
              type="button"
              className="tasks-page__button"
              onClick={handleCreateNew}
              disabled={isBusy || !canCreate || !canRead}
            >
              Nova tarefa
            </button>
          </div>
        </div>
      )}

      {error ? <div className="tasks-page__alert">{error}</div> : null}
      {success ? (
        <div className="tasks-page__alert tasks-page__alert--success">
          {success}
        </div>
      ) : null}
      {!canRead || !canCreate || !canDelete ? (
        <div className="tasks-page__alert tasks-page__alert--info">
          {[
            canRead ? null : "leitura desabilitada",
            canCreate ? null : "criação desabilitada",
            canDelete ? null : "exclusão desabilitada",
          ]
            .filter(Boolean)
            .join(" - ")}
        </div>
      ) : null}

      <div className="tasks-page__layout">
        <TasksTable
          items={canRead ? filteredItems : []}
          loading={canRead ? loading : false}
          selectedId={selected?.id}
          canEdit={canRead && canUpdate}
          canDelete={canRead && canDelete}
          projectOptions={projectOptions}
          parentTaskOptions={parentTaskOptions}
          userOptions={userOptions}
          onSelect={handleSelect}
          onDelete={(item) => void handleDelete(item)}
        />

        <TaskForm
          value={draft}
          editing={!!selected}
          canEditFields={canRead && (selected ? canUpdate : canCreate)}
          canSubmit={canRead && canSubmitCurrent}
          saving={saving}
          projectOptions={projectOptions}
          projectAccess={projectAccess}
          parentTaskOptions={filteredParentTaskOptions}
          parentTaskAccess={parentTaskAccess}
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
