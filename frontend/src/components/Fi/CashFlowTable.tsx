import type { CashFlow } from "../../pages/Fi/CashFlowPage";

type CashFlowTableProps = {
  items: CashFlow[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: CashFlow) => void;
  onDelete: (item: CashFlow) => void;
};

export default function CashFlowTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: CashFlowTableProps) {
  return (
    <section className="cash-flow-table">
      <div className="cash-flow-table__head">
        <h3 className="cash-flow-table__title">Lista de fluxos</h3>
        <span className="cash-flow-table__meta">{items.length} registros</span>
      </div>

      <div className="cash-flow-table__wrap">
        <table className="cash-flow-table__table">
          <thead>
            <tr>
              <th>Data</th>
              <th>Saldo inicial</th>
              <th>Previsto</th>
              <th>Realizado</th>
              <th>Saldo previsto</th>
              <th>Saldo real</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} className="cash-flow-table__empty">
                  Carregando fluxo de caixa...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={7} className="cash-flow-table__empty">
                  Nenhum fluxo de caixa encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.dataReferencia}-${item.createdAt}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.dataReferencia || "-"}</td>
                  <td>{item.saldoInicial || "0"}</td>
                  <td className="cash-flow-table__compound">
                    <span>+ {item.entradasPrevistas || "0"}</span>
                    <span>- {item.saidasPrevistas || "0"}</span>
                  </td>
                  <td className="cash-flow-table__compound">
                    <span>+ {item.entradasRealizadas || "0"}</span>
                    <span>- {item.saidasRealizadas || "0"}</span>
                  </td>
                  <td>
                    <strong>{item.saldoFinalPrevisto || "0"}</strong>
                  </td>
                  <td>
                    <strong>{item.saldoFinalReal || "0"}</strong>
                  </td>
                  <td className="cash-flow-table__actions">
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
                      <span className="cash-flow-table__empty-action">
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
