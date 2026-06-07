import type { EsocialEventEntry } from "../../pages/Fiscal/EsocialEventsPage";

type EsocialEventsTableProps = {
  items: EsocialEventEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: EsocialEventEntry) => void;
  onDelete: (item: EsocialEventEntry) => void;
};

export default function EsocialEventsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: EsocialEventsTableProps) {
  return (
    <section className="esocial-events-table">
      <div className="esocial-events-page__panel-head">
        <h3 className="esocial-events-page__panel-title">Eventos eSocial</h3>
        <span className="esocial-events-page__panel-subtitle">
          {items.length} registros
        </span>
      </div>

      <div className="esocial-events-page__table-wrap">
        <table className="esocial-events-page__table">
          <thead>
            <tr>
              <th>Evento</th>
              <th>Apuracao</th>
              <th>Status</th>
              <th>Conteudo</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="esocial-events-page__empty">
                  Carregando eventos eSocial...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="esocial-events-page__empty">
                  Nenhum evento eSocial encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.tipoEvento}-${item.eventoId}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="esocial-events-page__identity">
                      <strong>{item.tipoEvento || "-"}</strong>
                      <span>Evento ID: {item.eventoId || "-"}</span>
                    </div>
                  </td>
                  <td>{item.periodoApuracao || "-"}</td>
                  <td>{item.status || "-"}</td>
                  <td>
                    <div className="esocial-events-page__details">
                      <span>
                        {item.conteudo.length > 80
                          ? `${item.conteudo.slice(0, 80)}...`
                          : item.conteudo || "-"}
                      </span>
                    </div>
                  </td>
                  <td className="esocial-events-page__actions">
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
                      <span className="esocial-events-page__empty-action">
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
