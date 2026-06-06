import type { Permission } from "../../pages/Sys/PermissionsPage";

type PermissionTableProps = {
  items: Permission[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: Permission) => void;
  onDelete: (item: Permission) => void;
};

export default function PermissionTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: PermissionTableProps) {
  return (
    <section className="permission-table">
      <div className="permission-table__head">
        <h3 className="permission-table__title">Lista de permissoes</h3>
        <span className="permission-table__meta">{items.length} registros</span>
      </div>

      <div className="permission-table__wrap">
        <table className="permission-table__table">
          <thead>
            <tr>
              <th>Permissao</th>
              <th>Escopo</th>
              <th>Acao</th>
              <th>Criado em</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="permission-table__empty">
                  Carregando permissoes...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="permission-table__empty">
                  Nenhuma permissao encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.nome}-${item.modulo}-${item.recurso}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="permission-table__identity">
                      <strong>{item.nome || "-"}</strong>
                      <span>{item.descricao || "Sem descricao"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="permission-table__scope">
                      <strong>{item.modulo || "-"}</strong>
                      <span>{item.recurso || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <span className="permission-table__badge">
                      {item.acao || "-"}
                    </span>
                  </td>
                  <td>{item.createdAt ? item.createdAt.slice(0, 10) : "-"}</td>
                  <td className="permission-table__actions">
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
                      <span className="permission-table__empty-action">
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
