import type {
  AllocatedResource,
  ProjectOption,
  TaskOption,
} from "../../pages/Ps/AllocatedResourcesPage";

type AllocatedResourcesTableProps = {
  items: AllocatedResource[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  projectOptions: ProjectOption[];
  taskOptions: TaskOption[];
  onSelect: (item: AllocatedResource) => void;
  onDelete: (item: AllocatedResource) => void;
};

function resolveProjectLabel(projectId: string, projectOptions: ProjectOption[]) {
  const match = projectOptions.find((option) => String(option.id) === projectId);
  return match ? match.label : projectId ? `Projeto #${projectId}` : "-";
}

function resolveTaskLabel(taskId: string, taskOptions: TaskOption[]) {
  const match = taskOptions.find((option) => String(option.id) === taskId);
  return match ? match.label : taskId ? `Tarefa #${taskId}` : "-";
}

export default function AllocatedResourcesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  projectOptions,
  taskOptions,
  onSelect,
  onDelete,
}: AllocatedResourcesTableProps) {
  return (
    <section className="allocated-resources-table">
      <div className="allocated-resources-table__head">
        <h3 className="allocated-resources-table__title">Recursos alocados</h3>
        <span className="allocated-resources-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="allocated-resources-table__wrap">
        <table className="allocated-resources-table__table">
          <thead>
            <tr>
              <th>Contexto</th>
              <th>Recurso</th>
              <th>Medicao</th>
              <th>Financeiro</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="allocated-resources-table__empty">
                  Carregando recursos alocados...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="allocated-resources-table__empty">
                  Nenhum recurso alocado encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.tipoRecurso}-${item.recursoId}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="allocated-resources-table__details">
                      <span>{resolveProjectLabel(item.projeto, projectOptions)}</span>
                      <span>{resolveTaskLabel(item.tarefa, taskOptions)}</span>
                    </div>
                  </td>
                  <td>
                    <div className="allocated-resources-table__identity">
                      <strong>{item.tipoRecurso || "-"}</strong>
                      <span>Recurso #{item.recursoId || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="allocated-resources-table__details">
                      <span>Quantidade: {item.quantidade || "0"}</span>
                      <span>Data: {item.dataAlocacao || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="allocated-resources-table__details">
                      <span>Unitario: {item.valorUnitario || "0"}</span>
                      <span>Total: {item.valorTotal || "0"}</span>
                    </div>
                  </td>
                  <td className="allocated-resources-table__actions">
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
                      <span className="allocated-resources-table__empty-action">
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
