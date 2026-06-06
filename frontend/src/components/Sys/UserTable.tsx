import type { User } from "../../pages/Sys/UsersPage";

type UserTableProps = {
  items: User[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: User) => void;
  onDelete: (item: User) => void;
};

export default function UserTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: UserTableProps) {
  return (
    <section className="user-table">
      <div className="user-table__head">
        <h3 className="user-table__title">Lista de usuarios</h3>
        <span className="user-table__meta">{items.length} registros</span>
      </div>

      <div className="user-table__wrap">
        <table className="user-table__table">
          <thead>
            <tr>
              <th>Usuario</th>
              <th>Login</th>
              <th>Tipo</th>
              <th>Status</th>
              <th>Tentativas</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} className="user-table__empty">
                  Carregando usuarios...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={6} className="user-table__empty">
                  Nenhum usuario encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.login}-${item.email}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="user-table__identity">
                      <strong>{item.nome || "-"}</strong>
                      <span>{item.email || "Sem e-mail"}</span>
                    </div>
                  </td>
                  <td>{item.login || "-"}</td>
                  <td>{item.tipoUsuario || "-"}</td>
                  <td>
                    <span
                      className={
                        item.status === "ativo"
                          ? "user-table__badge user-table__badge--active"
                          : "user-table__badge"
                      }
                    >
                      {item.status || "-"}
                    </span>
                  </td>
                  <td>{item.tentativasLogin || "0"}</td>
                  <td className="user-table__actions">
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
                      <span className="user-table__empty-action">Sem acoes</span>
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
