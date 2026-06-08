import type { AuditRecord } from "../../pages/Grc/AuditsPage";

type AuditsTableProps = {
  items: AuditRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: AuditRecord) => void;
  onDelete: (item: AuditRecord) => void;
};

export default function AuditsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: AuditsTableProps) {
  return (
    <section className="audits-table">
      <div className="audits-table__head">
        <h3 className="audits-table__title">Auditorias</h3>
        <span className="audits-table__meta">{items.length} registros</span>
      </div>

      <div className="audits-table__wrap">
        <table className="audits-table__table">
          <thead>
            <tr>
              <th>Auditoria</th>
              <th>Planejamento</th>
              <th>Execucao</th>
              <th>Acoes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="audits-table__empty">
                  Carregando auditorias...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="audits-table__empty">
                  Nenhuma auditoria encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.titulo}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="audits-table__details">
                      <strong>{item.titulo || "Sem titulo"}</strong>
                      <span>{item.tipoAuditoria || "-"}</span>
                      <span>{item.escopo || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="audits-table__details">
                      <strong>{item.status || "-"}</strong>
                      <span>Inicio: {item.dataInicio || "-"}</span>
                      <span>Fim: {item.dataFim || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="audits-table__details">
                      <strong>
                        {item.responsavelId.trim() !== ""
                          ? `Responsavel #${item.responsavelId}`
                          : "Sem responsavel"}
                      </strong>
                    </div>
                  </td>
                  <td className="audits-table__actions">
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
                      <span className="audits-table__empty-action">
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
