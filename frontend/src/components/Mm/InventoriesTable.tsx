import type { InventoryRecord } from "../../pages/Mm/InventoriesPage";

type InventoriesTableProps = {
  items: InventoryRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: InventoryRecord) => void;
  onDelete: (item: InventoryRecord) => void;
};

export default function InventoriesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: InventoriesTableProps) {
  return (
    <section className="inventories-table">
      <div className="inventories-table__head">
        <h3 className="inventories-table__title">Inventarios</h3>
        <span className="inventories-table__meta">{items.length} registros</span>
      </div>

      <div className="inventories-table__wrap">
        <table className="inventories-table__table">
          <thead>
            <tr>
              <th>Ciclo</th>
              <th>Calendario</th>
              <th>Contexto</th>
              <th>Acoes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="inventories-table__empty">
                  Carregando inventarios...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="inventories-table__empty">
                  Nenhum inventario encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.tipoInventario}-${item.dataInicio}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="inventories-table__details">
                      <strong>{item.tipoInventario || "-"}</strong>
                      <span>Status: {item.status || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="inventories-table__details">
                      <strong>Inicio: {item.dataInicio || "-"}</strong>
                      <span>Fim: {item.dataFim || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="inventories-table__details">
                      <strong>Observacoes</strong>
                      <span>{item.observacoes || "-"}</span>
                    </div>
                  </td>
                  <td className="inventories-table__actions">
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
                      <span className="inventories-table__empty-action">
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
