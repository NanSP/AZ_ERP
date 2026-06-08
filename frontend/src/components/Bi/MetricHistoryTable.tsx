import type {
  MetricHistoryRecord,
  MetricOption,
} from "../../pages/Bi/MetricHistoryPage";

type MetricHistoryTableProps = {
  items: MetricHistoryRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  metricOptions: MetricOption[];
  onSelect: (item: MetricHistoryRecord) => void;
  onDelete: (item: MetricHistoryRecord) => void;
};

function resolveMetricLabel(id: string, options: MetricOption[]) {
  const match = options.find((option) => String(option.id) === id);
  return match ? match.label : id ? `Metrica #${id}` : "-";
}

export default function MetricHistoryTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  metricOptions,
  onSelect,
  onDelete,
}: MetricHistoryTableProps) {
  return (
    <section className="metric-history-table">
      <div className="metric-history-table__head">
        <h3 className="metric-history-table__title">Historico de metricas</h3>
        <span className="metric-history-table__meta">{items.length} registros</span>
      </div>

      <div className="metric-history-table__wrap">
        <table className="metric-history-table__table">
          <thead>
            <tr>
              <th>Metrica</th>
              <th>Periodo</th>
              <th>Valor</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="metric-history-table__empty">
                  Carregando historicos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="metric-history-table__empty">
                  Nenhum historico encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.metrica}-${item.periodo}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{resolveMetricLabel(item.metrica, metricOptions)}</td>
                  <td>{item.periodo || "-"}</td>
                  <td>{item.valorApurado || "-"}</td>
                  <td className="metric-history-table__actions">
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
                      <span className="metric-history-table__empty-action">
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
