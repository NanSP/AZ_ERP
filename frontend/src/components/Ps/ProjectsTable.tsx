import type {
  PartnerOption,
  Project,
  UserOption,
} from "../../pages/Ps/ProjectsPage";

type ProjectsTableProps = {
  items: Project[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  partnerOptions: PartnerOption[];
  userOptions: UserOption[];
  onSelect: (item: Project) => void;
  onDelete: (item: Project) => void;
};

function resolvePartnerLabel(
  clienteId: string,
  partnerOptions: PartnerOption[],
) {
  const match = partnerOptions.find(
    (option) => String(option.id) === clienteId,
  );
  return match ? match.label : clienteId ? `Cliente #${clienteId}` : "-";
}

function resolveUserLabel(gerenteId: string, userOptions: UserOption[]) {
  const match = userOptions.find((option) => String(option.id) === gerenteId);
  return match ? match.label : gerenteId ? `Gerente #${gerenteId}` : "-";
}

export default function ProjectsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  partnerOptions,
  userOptions,
  onSelect,
  onDelete,
}: ProjectsTableProps) {
  return (
    <section className="projects-table">
      <div className="projects-table__head">
        <h3 className="projects-table__title">Projetos</h3>
        <span className="projects-table__meta">{items.length} registros</span>
      </div>

      <div className="projects-table__wrap">
        <table className="projects-table__table">
          <thead>
            <tr>
              <th>Projeto</th>
              <th>Responsáveis</th>
              <th>Cronograma</th>
              <th>Orçamento</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="projects-table__empty">
                  Carregando projetos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="projects-table__empty">
                  Nenhum projeto encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.codigo ?? item.nome}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="projects-table__identity">
                      <strong>{item.nome || "-"}</strong>
                      <span>{item.codigo || "Sem codigo"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="projects-table__details">
                      <span>
                        {resolvePartnerLabel(item.cliente, partnerOptions)}
                      </span>
                      <span>{resolveUserLabel(item.gerente, userOptions)}</span>
                    </div>
                  </td>
                  <td>
                    <div className="projects-table__details">
                      <span>
                        Inicio:{" "}
                        {item.dataInicio || item.dataPrevistaInicio || "-"}
                      </span>
                      <span>
                        Fim: {item.dataFim || item.dataPrevistaFim || "-"}
                      </span>
                    </div>
                  </td>
                  <td>
                    <div className="projects-table__details">
                      <span>Total: {item.orcamentoTotal || "0"}</span>
                      <span>Gasto: {item.orcamentoGasto || "0"}</span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.status === "concluido"
                          ? "projects-table__badge projects-table__badge--done"
                          : item.status === "em_andamento"
                            ? "projects-table__badge projects-table__badge--progress"
                            : "projects-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td className="projects-table__actions">
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
                      <span className="projects-table__empty-action">
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
