import type { MetricRecord } from "../../pages/Bi/MetricsPage";

type MetricsTableProps = {
  items: MetricRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: MetricRecord) => void;
  onDelete: (item: MetricRecord) => void;
};

function summarizeFormula(value: string) {
  const normalized = value.trim().replace(/\s+/g, " ");
  if (!normalized) {
    return "-";
  }

  return normalized.length > 80 ? `${normalized.slice(0, 80)}...` : normalized;
}

export default function MetricsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: MetricsTableProps) {
  return (
    <section className="metrics-table">
      <div className="metrics-table__head">
        <h3 className="metrics-table__title">Metricas</h3>
        <span className="metrics-table__meta">{items.length} registros</span>
      </div>

      <div className="metrics-table__wrap">
        <table className="metrics-table__table">
          <thead>
            <tr>
              <th>Indicador</th>
              <th>Fórmula</th>
              <th>Meta</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="metrics-table__empty">
                  Carregando métricas...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="metrics-table__empty">
                  Nenhuma métrica encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.nome}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="metrics-table__details">
                      <strong>{item.nome || "Sem nome"}</strong>
                      <span>{item.categoria || "-"}</span>
                      <span>{item.unidadeMedida || "-"}</span>
                    </div>
                  </td>
                  <td className="metrics-table__formula">
                    {summarizeFormula(item.formula)}
                  </td>
                  <td>
                    <div className="metrics-table__details">
                      <strong>{item.meta || "-"}</strong>
                      <span>{item.descricao || "-"}</span>
                    </div>
                  </td>
                  <td className="metrics-table__actions">
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
                      <span className="metrics-table__empty-action">
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
