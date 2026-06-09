import type { MaterialRecord } from "../../pages/Mm/MaterialsPage";

type MaterialsTableProps = {
  items: MaterialRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: MaterialRecord) => void;
  onDelete: (item: MaterialRecord) => void;
};

export default function MaterialsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: MaterialsTableProps) {
  return (
    <section className="materials-table">
      <div className="materials-table__head">
        <h3 className="materials-table__title">Materiais</h3>
        <span className="materials-table__meta">{items.length} registros</span>
      </div>

      <div className="materials-table__wrap">
        <table className="materials-table__table">
          <thead>
            <tr>
              <th>Produto</th>
              <th>Classificação</th>
              <th>Detalhes</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="materials-table__empty">
                  Carregando materiais...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="materials-table__empty">
                  Nenhum material encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.produtoId}-${item.tipoMaterial}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="materials-table__details">
                      <strong>Produto #{item.produtoId || "-"}</strong>
                      <span>{item.tipoMaterial || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="materials-table__details">
                      <strong>{item.categoria || "-"}</strong>
                      <span>{item.subcategoria || "-"}</span>
                      <span>{item.classePerigo || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="materials-table__details">
                      <strong>{item.marca || "-"}</strong>
                      <span>{item.modelo || "-"}</span>
                      <span>{item.condicaoArmazenamento || "-"}</span>
                    </div>
                  </td>
                  <td className="materials-table__actions">
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
                      <span className="materials-table__empty-action">
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
