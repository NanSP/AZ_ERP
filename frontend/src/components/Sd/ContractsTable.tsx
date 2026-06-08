import type { ContractRecord } from "../../pages/Sd/ContractsPage";

type ContractsTableProps = {
  items: ContractRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: ContractRecord) => void;
  onDelete: (item: ContractRecord) => void;
};

export default function ContractsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: ContractsTableProps) {
  return (
    <section className="contracts-table">
      <div className="contracts-table__head">
        <h3 className="contracts-table__title">Contratos</h3>
        <span className="contracts-table__meta">{items.length} registros</span>
      </div>

      <div className="contracts-table__wrap">
        <table className="contracts-table__table">
          <thead>
            <tr>
              <th>Formalizacao</th>
              <th>Vigencia</th>
              <th>Comercial</th>
              <th>Acoes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="contracts-table__empty">
                  Carregando contratos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="contracts-table__empty">
                  Nenhum contrato encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.clienteId}-${item.numeroContrato}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="contracts-table__details">
                      <strong>{item.numeroContrato || "-"}</strong>
                      <span>Cliente parceiro #{item.clienteId || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="contracts-table__details">
                      <strong>Inicio: {item.dataInicio || "-"}</strong>
                      <span>Fim: {item.dataFim || "-"}</span>
                      <span>Status: {item.status || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="contracts-table__details">
                      <strong>Valor: {item.valorTotal || "-"}</strong>
                      <span>{item.objeto || "-"}</span>
                    </div>
                  </td>
                  <td className="contracts-table__actions">
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
                      <span className="contracts-table__empty-action">
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
