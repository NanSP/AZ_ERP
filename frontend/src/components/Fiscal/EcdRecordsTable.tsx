import type { EcdRecordEntry } from "../../pages/Fiscal/EcdRecordsPage";

type EcdRecordsTableProps = {
  items: EcdRecordEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: EcdRecordEntry) => void;
  onDelete: (item: EcdRecordEntry) => void;
};

export default function EcdRecordsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: EcdRecordsTableProps) {
  return (
    <section className="ecd-records-table">
      <div className="ecd-records-page__panel-head">
        <h3 className="ecd-records-page__panel-title">Registros ECD</h3>
        <span className="ecd-records-page__panel-subtitle">
          {items.length} registros
        </span>
      </div>

      <div className="ecd-records-page__table-wrap">
        <table className="ecd-records-page__table">
          <thead>
            <tr>
              <th>Registro</th>
              <th>Periodo</th>
              <th>Conteudo</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="ecd-records-page__empty">
                  Carregando registros ECD...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="ecd-records-page__empty">
                  Nenhum registro ECD encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.registro}-${item.periodo}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{item.registro || "-"}</td>
                  <td>{item.periodo || "-"}</td>
                  <td>
                    <div className="ecd-records-page__details">
                      <span>
                        {item.conteudo.length > 100
                          ? `${item.conteudo.slice(0, 100)}...`
                          : item.conteudo || "-"}
                      </span>
                    </div>
                  </td>
                  <td className="ecd-records-page__actions">
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
                      <span className="ecd-records-page__empty-action">
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
