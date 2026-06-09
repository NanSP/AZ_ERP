import type {
  EmployeeOption,
  ProductionEntry,
  ProductionOrderOption,
} from "../../pages/Pp/ProductionEntriesPage";

type ProductionEntriesTableProps = {
  items: ProductionEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  orderOptions: ProductionOrderOption[];
  employeeOptions: EmployeeOption[];
  onSelect: (item: ProductionEntry) => void;
  onDelete: (item: ProductionEntry) => void;
};

function resolveLabel(
  id: string,
  options: Array<{ id: number; label: string }>,
  fallback: string,
) {
  const match = options.find((option) => String(option.id) === id);
  return match ? match.label : id ? `${fallback} #${id}` : "-";
}

export default function ProductionEntriesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  orderOptions,
  employeeOptions,
  onSelect,
  onDelete,
}: ProductionEntriesTableProps) {
  return (
    <section className="production-entries-table">
      <div className="production-entries-table__head">
        <h3 className="production-entries-table__title">Apontamentos</h3>
        <span className="production-entries-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="production-entries-table__wrap">
        <table className="production-entries-table__table">
          <thead>
            <tr>
              <th>Execução</th>
              <th>Janela</th>
              <th>Quantidade</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="production-entries-table__empty">
                  Carregando apontamentos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="production-entries-table__empty">
                  Nenhum apontamento encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={
                    item.id ??
                    `${item.op}-${item.createdAt ?? item.dataHoraInicio}`
                  }
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="production-entries-table__details">
                      <strong>
                        {resolveLabel(item.op, orderOptions, "OP")}
                      </strong>
                      <span>
                        {resolveLabel(
                          item.operador,
                          employeeOptions,
                          "Operador",
                        )}
                      </span>
                      <span>Máquina: {item.maquinaId || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="production-entries-table__details">
                      <span>Inicio: {item.dataHoraInicio || "-"}</span>
                      <span>Fim: {item.dataHoraFim || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="production-entries-table__details">
                      <span>Produzida: {item.quantidadeProduzida || "-"}</span>
                      <span>Refugo: {item.quantidadeRefugo || "-"}</span>
                      <span>Parada: {item.tempoParado || "-"}</span>
                    </div>
                  </td>
                  <td className="production-entries-table__actions">
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
                      <span className="production-entries-table__empty-action">
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
