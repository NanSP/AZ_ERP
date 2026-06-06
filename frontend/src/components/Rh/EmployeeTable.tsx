import type { Employee } from "../../pages/Rh/EmployeesPage";

type EmployeeTableProps = {
  items: Employee[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: Employee) => void;
  onDelete: (item: Employee) => void;
};

export default function EmployeeTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: EmployeeTableProps) {
  return (
    <section className="employee-table">
      <div className="employee-table__head">
        <h3 className="employee-table__title">Lista de colaboradores</h3>
        <span className="employee-table__meta">{items.length} registros</span>
      </div>

      <div className="employee-table__wrap">
        <table className="employee-table__table">
          <thead>
            <tr>
              <th>Colaborador</th>
              <th>CPF</th>
              <th>Cargo</th>
              <th>Departamento</th>
              <th>Situacao</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="employee-table__empty">
                  Carregando colaboradores...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="employee-table__empty">
                  Nenhum colaborador encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.cpf}-${item.nome}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="employee-table__identity">
                      <strong>{item.nome || "-"}</strong>
                      <span>{item.codigo || "Sem codigo"}</span>
                    </div>
                  </td>
                  <td>{item.cpf || "-"}</td>
                  <td>{item.cargo || "-"}</td>
                  <td>{item.departamento || "-"}</td>
                  <td>
                    <span
                      className={
                        item.situacao === "ativo"
                          ? "employee-table__badge employee-table__badge--active"
                          : item.situacao === "desligado"
                            ? "employee-table__badge employee-table__badge--inactive"
                            : "employee-table__badge"
                      }
                    >
                      {item.situacao || "-"}
                    </span>
                  </td>
                  <td className="employee-table__actions">
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
                      <span className="employee-table__empty-action">
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
