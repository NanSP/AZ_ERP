import type {
  EmployeeOption,
  TimeTrackingEntry,
} from "../../pages/Rh/TimeTrackingPage";

type TimeTrackingTableProps = {
  items: TimeTrackingEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  employeeOptions: EmployeeOption[];
  onSelect: (item: TimeTrackingEntry) => void;
  onDelete: (item: TimeTrackingEntry) => void;
};

function resolveEmployeeLabel(
  employeeId: string,
  employeeOptions: EmployeeOption[],
) {
  const match = employeeOptions.find(
    (option) => String(option.id) === employeeId,
  );
  return match ? match.label : `Colaborador #${employeeId || "-"}`;
}

export default function TimeTrackingTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  employeeOptions,
  onSelect,
  onDelete,
}: TimeTrackingTableProps) {
  return (
    <section className="time-tracking-table">
      <div className="time-tracking-table__head">
        <h3 className="time-tracking-table__title">Registros de ponto</h3>
        <span className="time-tracking-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="time-tracking-table__wrap">
        <table className="time-tracking-table__table">
          <thead>
            <tr>
              <th>Colaborador</th>
              <th>Data</th>
              <th>Jornada</th>
              <th>Horas</th>
              <th>Atrasos</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="time-tracking-table__empty">
                  Carregando registros de ponto...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="time-tracking-table__empty">
                  Nenhum registro de ponto encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.colaborador}-${item.data}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="time-tracking-table__identity">
                      <strong>
                        {resolveEmployeeLabel(
                          item.colaborador,
                          employeeOptions,
                        )}
                      </strong>
                      <span>ID #{item.colaborador || "-"}</span>
                    </div>
                  </td>
                  <td>{item.data || "-"}</td>
                  <td>
                    <div className="time-tracking-table__schedule">
                      <span>Entrada: {item.horaEntrada || "-"}</span>
                      <span>
                        Almoco: {item.horaSaidaAlmoco || "-"} /{" "}
                        {item.horaRetornoAlmoco || "-"}
                      </span>
                      <span>Saída: {item.horaSaida || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="time-tracking-table__hours">
                      <span>Trab.: {item.horasTrabalhadas || "0"}</span>
                      <span>Extras: {item.horasExtras || "0"}</span>
                    </div>
                  </td>
                  <td>{item.atrasos ?? 0} min</td>
                  <td className="time-tracking-table__actions">
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
                      <span className="time-tracking-table__empty-action">
                        Sem ações
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
