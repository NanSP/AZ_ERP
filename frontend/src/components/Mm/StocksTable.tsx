import type { StockRecord } from "../../pages/Mm/StocksPage";

type StocksTableProps = {
  items: StockRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: StockRecord) => void;
  onDelete: (item: StockRecord) => void;
};

export default function StocksTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: StocksTableProps) {
  return (
    <section className="stocks-table">
      <div className="stocks-table__head">
        <h3 className="stocks-table__title">Estoques</h3>
        <span className="stocks-table__meta">{items.length} registros</span>
      </div>

      <div className="stocks-table__wrap">
        <table className="stocks-table__table">
          <thead>
            <tr>
              <th>Identificação</th>
              <th>Saldo</th>
              <th>Planejamento</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="stocks-table__empty">
                  Carregando estoques...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="stocks-table__empty">
                  Nenhum estoque encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={
                    item.id ??
                    `${item.produtoId}-${item.empresaId}-${item.localizacao}-${item.lote}`
                  }
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="stocks-table__details">
                      <strong>Produto #{item.produtoId || "-"}</strong>
                      <span>Empresa #{item.empresaId || "-"}</span>
                      <span>{item.localizacao || "-"}</span>
                      <span>{item.lote || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="stocks-table__details">
                      <strong>{item.quantidade || "0"}</strong>
                      <span>Valor unitario: {item.valorUnitario || "-"}</span>
                      <span>Validade: {item.dataValidade || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="stocks-table__details">
                      <strong>Min: {item.quantidadeMinima || "0"}</strong>
                      <span>Max: {item.quantidadeMaxima || "0"}</span>
                    </div>
                  </td>
                  <td className="stocks-table__actions">
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
                      <span className="stocks-table__empty-action">
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
