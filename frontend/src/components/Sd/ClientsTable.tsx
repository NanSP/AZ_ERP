import type { ClientRecord } from "../../pages/Sd/ClientsPage";

type ClientsTableProps = {
  items: ClientRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: ClientRecord) => void;
  onDelete: (item: ClientRecord) => void;
};

export default function ClientsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: ClientsTableProps) {
  return (
    <section className="clients-table">
      <div className="clients-table__head">
        <h3 className="clients-table__title">Clientes</h3>
        <span className="clients-table__meta">{items.length} registros</span>
      </div>

      <div className="clients-table__wrap">
        <table className="clients-table__table">
          <thead>
            <tr>
              <th>Base comercial</th>
              <th>Relacionamento</th>
              <th>Porte</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="clients-table__empty">
                  Carregando clientes...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="clients-table__empty">
                  Nenhum cliente encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.parceiroId}-${item.classificacao}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="clients-table__details">
                      <strong>Parceiro #{item.parceiroId || "-"}</strong>
                      <span>{item.classificacao || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="clients-table__details">
                      <strong>Origem: {item.origem || "-"}</strong>
                      <span>{item.website || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="clients-table__details">
                      <strong>
                        Faturamento: {item.faturamentoAnual || "-"}
                      </strong>
                      <span>
                        Funcionários: {item.numeroFuncionarios || "-"}
                      </span>
                    </div>
                  </td>
                  <td className="clients-table__actions">
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
                      <span className="clients-table__empty-action">
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
