import type {
  ParentTaskAccess,
  ParentTaskOption,
  ProjectAccess,
  ProjectOption,
  Task,
  UserAccess,
  UserOption,
} from "../../pages/Ps/TasksPage";

type TaskFormProps = {
  value: Task;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  projectOptions: ProjectOption[];
  projectAccess: ProjectAccess;
  parentTaskOptions: ParentTaskOption[];
  parentTaskAccess: ParentTaskAccess;
  userOptions: UserOption[];
  userAccess: UserAccess;
  onChange: (value: Task) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "pendente", label: "Pendente" },
  { value: "em_andamento", label: "Em andamento" },
  { value: "concluida", label: "Concluida" },
  { value: "cancelada", label: "Cancelada" },
];

const priorityOptions = [
  { value: "1", label: "1 - Baixa" },
  { value: "2", label: "2" },
  { value: "3", label: "3 - Media" },
  { value: "4", label: "4" },
  { value: "5", label: "5 - Alta" },
];

export default function TaskForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  projectOptions,
  projectAccess,
  parentTaskOptions,
  parentTaskAccess,
  userOptions,
  userAccess,
  onChange,
  onSave,
  onReset,
}: TaskFormProps) {
  const canSave =
    value.projeto.trim() !== "" &&
    value.titulo.trim() !== "" &&
    value.responsavel.trim() !== "";

  function update<K extends keyof Task>(field: K, fieldValue: Task[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="task-form">
      <div className="task-form__head">
        <div>
          <h3 className="task-form__title">
            {editing ? "Editar tarefa" : "Nova tarefa"}
          </h3>
          <p className="task-form__subtitle">
            Cadastre a tarefa com projeto, hierarquia, responsavel e progresso.
          </p>
          {editing && value.id ? (
            <p className="task-form__meta">Registro selecionado: #{value.id}</p>
          ) : !canEditFields ? (
            <p className="task-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="task-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="task-form__grid">
        <label className="task-form__field">
          <span>Projeto</span>
          {projectOptions.length > 0 ? (
            <select
              value={value.projeto}
              onChange={(event) => {
                update("projeto", event.target.value);
                onChange({
                  ...value,
                  projeto: event.target.value,
                  tarefaPai: "",
                });
              }}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {projectOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.projeto}
              onChange={(event) =>
                update("projeto", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do projeto"
              disabled={!canEditFields}
            />
          )}
          {projectAccess === "unavailable" ? (
            <small className="task-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="task-form__field">
          <span>Tarefa pai</span>
          {parentTaskOptions.length > 0 ? (
            <select
              value={value.tarefaPai}
              onChange={(event) => update("tarefaPai", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Sem tarefa pai</option>
              {parentTaskOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.tarefaPai}
              onChange={(event) =>
                update("tarefaPai", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da tarefa pai"
              disabled={!canEditFields}
            />
          )}
          {parentTaskAccess === "unavailable" ? (
            <small className="task-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="task-form__field task-form__field--span-2">
          <span>Titulo</span>
          <input
            value={value.titulo}
            onChange={(event) => update("titulo", event.target.value)}
            placeholder="Titulo da tarefa"
            disabled={!canEditFields}
          />
        </label>

        <label className="task-form__field task-form__field--span-2">
          <span>Descricao</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descricao da tarefa"
            disabled={!canEditFields}
          />
        </label>

        <label className="task-form__field">
          <span>Responsavel</span>
          {userOptions.length > 0 ? (
            <select
              value={value.responsavel}
              onChange={(event) => update("responsavel", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {userOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.responsavel}
              onChange={(event) =>
                update("responsavel", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do responsavel"
              disabled={!canEditFields}
            />
          )}
          {userAccess === "unavailable" ? (
            <small className="task-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="task-form__field">
          <span>Status</span>
          <select
            value={value.status}
            onChange={(event) => update("status", event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="task-form__field">
          <span>Data inicio</span>
          <input
            type="date"
            value={value.dataInicio}
            onChange={(event) => update("dataInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="task-form__field">
          <span>Data fim</span>
          <input
            type="date"
            value={value.dataFim}
            onChange={(event) => update("dataFim", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="task-form__field">
          <span>Horas estimadas</span>
          <input
            value={value.horasEstimadas}
            onChange={(event) =>
              update(
                "horasEstimadas",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="task-form__field">
          <span>Horas realizadas</span>
          <input
            value={value.horasRealizadas}
            onChange={(event) =>
              update(
                "horasRealizadas",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="task-form__field">
          <span>Percentual concluido</span>
          <input
            value={value.percentualConcluido}
            onChange={(event) =>
              update(
                "percentualConcluido",
                event.target.value.replace(/\D/g, "").slice(0, 3),
              )
            }
            placeholder="0 a 100"
            disabled={!canEditFields}
          />
        </label>

        <label className="task-form__field">
          <span>Prioridade</span>
          <select
            value={value.prioridade}
            onChange={(event) => update("prioridade", event.target.value)}
            disabled={!canEditFields}
          >
            {priorityOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button
        type="button"
        className="task-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar tarefa"}
      </button>
    </aside>
  );
}
