import type { SlaConfigEntry } from "../../pages/Sm/SlaConfigPage";

type SlaConfigsTableProps = {
  items: SlaConfigEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: SlaConfigEntry) => void;
  onDelete: (item: SlaConfigEntry) => void;
};

export default function SlaConfigsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: SlaConfigsTableProps) {
  return (
    <section className="sla-table">
      <div className="sla-table__head">
        <h3 className="sla-table__title">Configuracoes de SLA</h3>
        <span className="sla-table__meta">{items.length} registros</span>
      </div>

      <div className="sla-table__wrap">
        <table className="sla-table__table">
          <thead>
            <tr>
              <th>Servico</th>
              <th>Prioridade</th>
              <th>Atendimento</th>
              <th>Resolucao</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="sla-table__empty">
                  Carregando configuracoes de SLA...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="sla-table__empty">
                  Nenhuma configuracao de SLA encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.tipoServico}-${item.prioridade}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.tipoServico || "-"}</td>
                  <td>
                    <span className="sla-table__badge">
                      {item.prioridade || "-"}
                    </span>
                  </td>
                  <td>{item.tempoAtendimentoHoras || "0"} h</td>
                  <td>{item.tempoResolucaoHoras || "0"} h</td>
                  <td className="sla-table__actions">
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
                      <span className="sla-table__empty-action">Sem acoes</span>
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
