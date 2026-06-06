import type { EmployeeOption, PayrollEntry } from "../../pages/Rh/PayrollPage";

type PayrollTableProps = {
  items: PayrollEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  employeeOptions: EmployeeOption[];
  onSelect: (item: PayrollEntry) => void;
  onDelete: (item: PayrollEntry) => void;
};

function resolveEmployeeLabel(
  employeeId: string,
  employeeOptions: EmployeeOption[],
) {
  const match = employeeOptions.find((option) => String(option.id) === employeeId);
  return match ? match.label : `Colaborador #${employeeId || "-"}`;
}

export default function PayrollTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  employeeOptions,
  onSelect,
  onDelete,
}: PayrollTableProps) {
  return (
    <section className="payroll-table">
      <div className="payroll-table__head">
        <h3 className="payroll-table__title">Folhas registradas</h3>
        <span className="payroll-table__meta">{items.length} registros</span>
      </div>

      <div className="payroll-table__wrap">
        <table className="payroll-table__table">
          <thead>
            <tr>
              <th>Colaborador</th>
              <th>Competencia</th>
              <th>Horas</th>
              <th>Valores</th>
              <th>Status</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="payroll-table__empty">
                  Carregando folhas...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="payroll-table__empty">
                  Nenhuma folha encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.colaborador}-${item.competencia}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="payroll-table__identity">
                      <strong>
                        {resolveEmployeeLabel(item.colaborador, employeeOptions)}
                      </strong>
                      <span>ID #{item.colaborador || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="payroll-table__period">
                      <strong>{item.competencia || "-"}</strong>
                      <span>Pagamento: {item.dataPagamento || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="payroll-table__hours">
                      <span>Normais: {item.horasNormais || "0"}</span>
                      <span>Extras: {item.horasExtras || "0"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="payroll-table__values">
                      <span>Bruto: {item.valorBruto || "0"}</span>
                      <span>Liquido: {item.valorLiquido || "0"}</span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.status === "pago"
                          ? "payroll-table__badge payroll-table__badge--paid"
                          : item.status === "cancelado"
                            ? "payroll-table__badge payroll-table__badge--cancelled"
                            : "payroll-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td className="payroll-table__actions">
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
                      <span className="payroll-table__empty-action">
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
