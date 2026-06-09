import type { BankMovement } from "../../pages/Fi/BankMovementsPage";

type BankMovementTableProps = {
  items: BankMovement[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: BankMovement) => void;
  onDelete: (item: BankMovement) => void;
};

export default function BankMovementTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: BankMovementTableProps) {
  return (
    <section className="bank-movement-table">
      <div className="bank-movement-table__head">
        <h3 className="bank-movement-table__title">Lista de movimentações</h3>
        <span className="bank-movement-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="bank-movement-table__wrap">
        <table className="bank-movement-table__table">
          <thead>
            <tr>
              <th>Conta</th>
              <th>Tipo</th>
              <th>Valor</th>
              <th>Data</th>
              <th>Histórico</th>
              <th>Conciliação</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="bank-movement-table__empty">
                  Carregando movimentações bancárias...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={7} className="bank-movement-table__empty">
                  Nenhuma movimentação bancária encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={
                    item.id ?? `${item.contaBancariaId}-${item.dataMovimento}`
                  }
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>#{item.contaBancariaId || "-"}</td>
                  <td>{item.tipoMovimento || "-"}</td>
                  <td>{item.valor || "-"}</td>
                  <td>{item.dataMovimento || "-"}</td>
                  <td>{item.historico || "-"}</td>
                  <td>
                    <span
                      className={
                        item.conciliado
                          ? "bank-movement-table__badge bank-movement-table__badge--reconciled"
                          : "bank-movement-table__badge"
                      }
                    >
                      {item.conciliado ? "conciliado" : "pendente"}
                    </span>
                    {item.dataConciliacao ? (
                      <small className="bank-movement-table__meta-line">
                        {item.dataConciliacao}
                      </small>
                    ) : null}
                  </td>
                  <td className="bank-movement-table__actions">
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
                      <span className="bank-movement-table__empty-action">
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
