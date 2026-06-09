import type { OpportunityRecord } from "../../pages/Sd/OpportunitiesPage";

type OpportunitiesTableProps = {
  items: OpportunityRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: OpportunityRecord) => void;
  onDelete: (item: OpportunityRecord) => void;
};

export default function OpportunitiesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: OpportunitiesTableProps) {
  return (
    <section className="opportunities-table">
      <div className="opportunities-table__head">
        <h3 className="opportunities-table__title">Oportunidades</h3>
        <span className="opportunities-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="opportunities-table__wrap">
        <table className="opportunities-table__table">
          <thead>
            <tr>
              <th>Negociação</th>
              <th>Pipeline</th>
              <th>Previsão</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="opportunities-table__empty">
                  Carregando oportunidades...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="opportunities-table__empty">
                  Nenhuma oportunidade encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.clienteId}-${item.titulo}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="opportunities-table__details">
                      <strong>{item.titulo || "-"}</strong>
                      <span>Cliente #{item.clienteId || "-"}</span>
                      <span>Responsável #{item.responsavelId || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="opportunities-table__details">
                      <strong>Estágio: {item.estagio || "-"}</strong>
                      <span>Probabilidade: {item.probabilidade || "-"}%</span>
                      <span>{item.motivoPerda || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="opportunities-table__details">
                      <strong>Valor: {item.valorEstimado || "-"}</strong>
                      <span>
                        Fechamento: {item.dataPrevistaFechamento || "-"}
                      </span>
                    </div>
                  </td>
                  <td className="opportunities-table__actions">
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
                      <span className="opportunities-table__empty-action">
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
