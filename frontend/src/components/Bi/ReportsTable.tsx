import type { ReportRecord } from "../../pages/Bi/ReportsPage";

type ReportsTableProps = {
  items: ReportRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: ReportRecord) => void;
  onDelete: (item: ReportRecord) => void;
};

function summarize(value: string, limit = 90) {
  const normalized = value.trim().replace(/\s+/g, " ");
  if (!normalized) {
    return "-";
  }

  return normalized.length > limit ? `${normalized.slice(0, limit)}...` : normalized;
}

export default function ReportsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: ReportsTableProps) {
  return (
    <section className="reports-table">
      <div className="reports-table__head">
        <h3 className="reports-table__title">Relatorios</h3>
        <span className="reports-table__meta">{items.length} registros</span>
      </div>

      <div className="reports-table__wrap">
        <table className="reports-table__table">
          <thead>
            <tr>
              <th>Relatorio</th>
              <th>SQL</th>
              <th>Parametros</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="reports-table__empty">
                  Carregando relatorios...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="reports-table__empty">
                  Nenhum relatorio encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.nome}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="reports-table__details">
                      <strong>{item.nome || "Sem nome"}</strong>
                      <span>{item.tipoRelatorio || "-"}</span>
                      <span>{item.descricao || "-"}</span>
                    </div>
                  </td>
                  <td className="reports-table__code">
                    {summarize(item.querySql, 140)}
                  </td>
                  <td className="reports-table__code">
                    {summarize(item.parametros, 120)}
                  </td>
                  <td className="reports-table__actions">
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
                      <span className="reports-table__empty-action">
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
