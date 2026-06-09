import type { CostCenter } from "../../pages/Fi/CostCentersPage";

type CostCenterTableProps = {
  items: CostCenter[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: CostCenter) => void;
  onDelete: (item: CostCenter) => void;
};

export default function CostCenterTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: CostCenterTableProps) {
  return (
    <section className="cost-center-table">
      <div className="cost-center-table__head">
        <h3 className="cost-center-table__title">Lista de centros</h3>
        <span className="cost-center-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="cost-center-table__wrap">
        <table className="cost-center-table__table">
          <thead>
            <tr>
              <th>Código</th>
              <th>Nome</th>
              <th>Tipo</th>
              <th>Responsável</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="cost-center-table__empty">
                  Carregando centros de custo...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="cost-center-table__empty">
                  Nenhum centro de custo encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.codigo}-${item.nome}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.codigo || "-"}</td>
                  <td>{item.nome || "-"}</td>
                  <td>{item.tipo || "-"}</td>
                  <td>{item.responsavel || "-"}</td>
                  <td>
                    <span
                      className={
                        item.ativo
                          ? "cost-center-table__badge cost-center-table__badge--active"
                          : "cost-center-table__badge"
                      }
                    >
                      {item.ativo ? "ativo" : "inativo"}
                    </span>
                  </td>
                  <td className="cost-center-table__actions">
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
                      <span className="cost-center-table__empty-action">
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
