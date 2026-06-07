import type {
  Attendance,
  EmployeeOption,
  OrderOption,
} from "../../pages/Sm/AttendancesPage";

type AttendancesTableProps = {
  items: Attendance[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  orderOptions: OrderOption[];
  employeeOptions: EmployeeOption[];
  onSelect: (item: Attendance) => void;
  onDelete: (item: Attendance) => void;
};

function resolveLabel(
  id: string,
  options: Array<{ id: number; label: string }>,
  fallback: string,
) {
  const match = options.find((option) => String(option.id) === id);
  return match ? match.label : id ? `${fallback} #${id}` : "-";
}

export default function AttendancesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  orderOptions,
  employeeOptions,
  onSelect,
  onDelete,
}: AttendancesTableProps) {
  return (
    <section className="attendances-table">
      <div className="attendances-table__head">
        <h3 className="attendances-table__title">Atendimentos</h3>
        <span className="attendances-table__meta">{items.length} registros</span>
      </div>

      <div className="attendances-table__wrap">
        <table className="attendances-table__table">
          <thead>
            <tr>
              <th>Atendimento</th>
              <th>Relacionamentos</th>
              <th>Horas</th>
              <th>Materiais</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="attendances-table__empty">
                  Carregando atendimentos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="attendances-table__empty">
                  Nenhum atendimento encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.os}-${item.dataHora}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="attendances-table__identity">
                      <strong>#{item.id ?? "-"}</strong>
                      <span>{item.descricao || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="attendances-table__details">
                      <span>{resolveLabel(item.os, orderOptions, "OS")}</span>
                      <span>
                        {resolveLabel(item.tecnico, employeeOptions, "Tecnico")}
                      </span>
                      <span>{item.dataHora || "-"}</span>
                    </div>
                  </td>
                  <td>{item.horasGastas || "0"}</td>
                  <td>
                    <span className="attendances-table__materials">
                      {item.materiaisUtilizados.trim() !== ""
                        ? "JSON informado"
                        : "Sem materiais"}
                    </span>
                  </td>
                  <td className="attendances-table__actions">
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
                      <span className="attendances-table__empty-action">
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
