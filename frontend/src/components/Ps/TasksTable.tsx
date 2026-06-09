import type {
  ParentTaskOption,
  ProjectOption,
  Task,
  UserOption,
} from "../../pages/Ps/TasksPage";

type TasksTableProps = {
  items: Task[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  projectOptions: ProjectOption[];
  parentTaskOptions: ParentTaskOption[];
  userOptions: UserOption[];
  onSelect: (item: Task) => void;
  onDelete: (item: Task) => void;
};

function resolveProjectLabel(
  projectId: string,
  projectOptions: ProjectOption[],
) {
  const match = projectOptions.find(
    (option) => String(option.id) === projectId,
  );
  return match ? match.label : projectId ? `Projeto #${projectId}` : "-";
}

function resolveParentTaskLabel(
  taskId: string,
  parentTaskOptions: ParentTaskOption[],
) {
  const match = parentTaskOptions.find(
    (option) => String(option.id) === taskId,
  );
  return match ? match.label : taskId ? `Tarefa #${taskId}` : "-";
}

function resolveUserLabel(userId: string, userOptions: UserOption[]) {
  const match = userOptions.find((option) => String(option.id) === userId);
  return match ? match.label : userId ? `Responsavel #${userId}` : "-";
}

export default function TasksTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  projectOptions,
  parentTaskOptions,
  userOptions,
  onSelect,
  onDelete,
}: TasksTableProps) {
  return (
    <section className="tasks-table">
      <div className="tasks-table__head">
        <h3 className="tasks-table__title">Tarefas</h3>
        <span className="tasks-table__meta">{items.length} registros</span>
      </div>

      <div className="tasks-table__wrap">
        <table className="tasks-table__table">
          <thead>
            <tr>
              <th>Tarefa</th>
              <th>Contexto</th>
              <th>Responsável</th>
              <th>Progresso</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="tasks-table__empty">
                  Carregando tarefas...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="tasks-table__empty">
                  Nenhuma tarefa encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.titulo}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="tasks-table__identity">
                      <strong>{item.titulo || "-"}</strong>
                      <span>{item.descricao || "Sem descricao"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="tasks-table__details">
                      <span>
                        {resolveProjectLabel(item.projeto, projectOptions)}
                      </span>
                      <span>
                        {resolveParentTaskLabel(
                          item.tarefaPai,
                          parentTaskOptions,
                        )}
                      </span>
                    </div>
                  </td>
                  <td>{resolveUserLabel(item.responsavel, userOptions)}</td>
                  <td>
                    <div className="tasks-table__details">
                      <span>{item.percentualConcluido || "0"}%</span>
                      <span>
                        {item.horasRealizadas || "0"} /{" "}
                        {item.horasEstimadas || "0"} h
                      </span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.status === "concluida"
                          ? "tasks-table__badge tasks-table__badge--done"
                          : item.status === "em_andamento"
                            ? "tasks-table__badge tasks-table__badge--progress"
                            : "tasks-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td className="tasks-table__actions">
                    {canEdit ? (
                      <button type="button" onClick={() => onSelect(item)}>
                        Editar
                      </button>
                    ) : null}
                    {canDelete ? (
                      <button type="button" onClick={() => onDelete(item)}>
                        Excluir
                      </button>
                    ) : null}
                    {!canEdit && !canDelete ? (
                      <span className="tasks-table__empty-action">
                        Sem acoes
                      </span>
                    ) : null}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
