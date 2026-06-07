import type { EfdRecordEntry } from "../../pages/Fiscal/EfdRecordsPage";

type EfdRecordsTableProps = {
  items: EfdRecordEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: EfdRecordEntry) => void;
  onDelete: (item: EfdRecordEntry) => void;
};

export default function EfdRecordsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: EfdRecordsTableProps) {
  return (
    <section className="efd-records-table">
      <div className="efd-records-page__panel-head">
        <h3 className="efd-records-page__panel-title">Registros EFD</h3>
        <span className="efd-records-page__panel-subtitle">
          {items.length} registros
        </span>
      </div>

      <div className="efd-records-page__table-wrap">
        <table className="efd-records-page__table">
          <thead>
            <tr>
              <th>Registro</th>
              <th>Periodo</th>
              <th>Conteudo</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="efd-records-page__empty">
                  Carregando registros EFD...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="efd-records-page__empty">
                  Nenhum registro EFD encontrado.
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
                    <div className="efd-records-page__details">
                      <span>
                        {item.conteudo.length > 100
                          ? `${item.conteudo.slice(0, 100)}...`
                          : item.conteudo || "-"}
                      </span>
                    </div>
                  </td>
                  <td className="efd-records-page__actions">
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
                      <span className="efd-records-page__empty-action">
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
