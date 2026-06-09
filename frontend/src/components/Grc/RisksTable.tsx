import type { RiskRecord } from "../../pages/Grc/RisksPage";

type RisksTableProps = {
  items: RiskRecord[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: RiskRecord) => void;
  onDelete: (item: RiskRecord) => void;
};

function getRiskLevelLabel(value: string) {
  if (value === "alto") {
    return "Alto";
  }

  if (value === "medio") {
    return "Medio";
  }

  return value === "baixo" ? "Baixo" : "-";
}

export default function RisksTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: RisksTableProps) {
  return (
    <section className="risks-table">
      <div className="risks-table__head">
        <h3 className="risks-table__title">Riscos</h3>
        <span className="risks-table__meta">{items.length} registros</span>
      </div>

      <div className="risks-table__wrap">
        <table className="risks-table__table">
          <thead>
            <tr>
              <th>Risco</th>
              <th>Avaliação</th>
              <th>Mitigação</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="risks-table__empty">
                  Carregando riscos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="risks-table__empty">
                  Nenhum risco encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.codigo ?? item.titulo}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="risks-table__details">
                      <strong>{item.titulo || "Sem titulo"}</strong>
                      <span>{item.codigo || "-"}</span>
                      <span>{item.categoria || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="risks-table__details">
                      <strong>{getRiskLevelLabel(item.nivelRisco)}</strong>
                      <span>Probabilidade: {item.probabilidade || "-"}</span>
                      <span>Impacto: {item.impacto || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="risks-table__details">
                      <strong>
                        {item.planoMitigacao.trim() !== ""
                          ? "Com plano"
                          : "Sem plano"}
                      </strong>
                      <span>
                        {item.responsavelId.trim() !== ""
                          ? `Responsavel #${item.responsavelId}`
                          : "Sem responsavel"}
                      </span>
                    </div>
                  </td>
                  <td className="risks-table__actions">
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
                      <span className="risks-table__empty-action">
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
