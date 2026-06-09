import type { DashboardRecord } from "../../pages/Bi/DashboardsPage";

type DashboardsTableProps = {
  items: DashboardRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: DashboardRecord) => void;
  onDelete: (item: DashboardRecord) => void;
};

function summarizeJson(value: string) {
  const normalized = value.trim().replace(/\s+/g, " ");
  if (!normalized) {
    return "-";
  }

  return normalized.length > 100
    ? `${normalized.slice(0, 100)}...`
    : normalized;
}

export default function DashboardsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: DashboardsTableProps) {
  return (
    <section className="dashboards-table">
      <div className="dashboards-table__head">
        <h3 className="dashboards-table__title">Dashboards</h3>
        <span className="dashboards-table__meta">{items.length} registros</span>
      </div>

      <div className="dashboards-table__wrap">
        <table className="dashboards-table__table">
          <thead>
            <tr>
              <th>Painel</th>
              <th>Layout</th>
              <th>Configurações</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="dashboards-table__empty">
                  Carregando dashboards...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="dashboards-table__empty">
                  Nenhum dashboard encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.nome}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="dashboards-table__details">
                      <strong>{item.nome || "Sem nome"}</strong>
                      <span>{item.descricao || "-"}</span>
                    </div>
                  </td>
                  <td className="dashboards-table__json">
                    {summarizeJson(item.layout)}
                  </td>
                  <td className="dashboards-table__json">
                    {summarizeJson(item.configuracoes)}
                  </td>
                  <td className="dashboards-table__actions">
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
                      <span className="dashboards-table__empty-action">
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
