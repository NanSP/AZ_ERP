import type { ConsentRecord } from "../../pages/Grc/ConsentsPage";

type ConsentsTableProps = {
  items: ConsentRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: ConsentRecord) => void;
  onDelete: (item: ConsentRecord) => void;
};

export default function ConsentsTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: ConsentsTableProps) {
  return (
    <section className="consents-table">
      <div className="consents-table__head">
        <h3 className="consents-table__title">Consentimentos</h3>
        <span className="consents-table__meta">{items.length} registros</span>
      </div>

      <div className="consents-table__wrap">
        <table className="consents-table__table">
          <thead>
            <tr>
              <th>Titular</th>
              <th>Finalidade</th>
              <th>Rastreabilidade</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="consents-table__empty">
                  Carregando consentimentos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="consents-table__empty">
                  Nenhum consentimento encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.titular}-${item.finalidade}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="consents-table__details">
                      <strong>#{item.titular || "-"}</strong>
                      <span>{item.tipoTitular || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="consents-table__details">
                      <strong>{item.finalidade || "-"}</strong>
                      <span>{item.dataRevogacao ? "Revogado" : "Ativo"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="consents-table__details">
                      <strong>{item.dataConsentimento || "-"}</strong>
                      <span>{item.ipAddress || "-"}</span>
                      <span>{item.userAgent || "-"}</span>
                    </div>
                  </td>
                  <td className="consents-table__actions">
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
                      <span className="consents-table__empty-action">
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
