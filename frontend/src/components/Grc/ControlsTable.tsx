import type { ControlRecord } from "../../pages/Grc/ControlsPage";

type ControlsTableProps = {
  items: ControlRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: ControlRecord) => void;
  onDelete: (item: ControlRecord) => void;
};

export default function ControlsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: ControlsTableProps) {
  return (
    <section className="controls-table">
      <div className="controls-table__head">
        <h3 className="controls-table__title">Controles</h3>
        <span className="controls-table__meta">{items.length} registros</span>
      </div>

      <div className="controls-table__wrap">
        <table className="controls-table__table">
          <thead>
            <tr>
              <th>Controle</th>
              <th>Operacao</th>
              <th>Governanca</th>
              <th>Acoes</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="controls-table__empty">
                  Carregando controles...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="controls-table__empty">
                  Nenhum controle encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.codigo ?? item.descricao}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="controls-table__details">
                      <strong>{item.codigo || "Sem codigo"}</strong>
                      <span>{item.descricao || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="controls-table__details">
                      <strong>{item.tipoControle || "-"}</strong>
                      <span>{item.frequencia || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="controls-table__details">
                      <strong>{item.efetivo ? "Efetivo" : "Em definicao"}</strong>
                      <span>
                        {item.responsavelId.trim() !== ""
                          ? `Responsavel #${item.responsavelId}`
                          : "Sem responsavel"}
                      </span>
                    </div>
                  </td>
                  <td className="controls-table__actions">
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
                      <span className="controls-table__empty-action">
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
