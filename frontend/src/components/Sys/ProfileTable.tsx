import type { Profile } from "../../pages/Sys/ProfilesPage";

type ProfileTableProps = {
  items: Profile[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  onSelect: (item: Profile) => void;
  onDelete: (item: Profile) => void;
};

export default function ProfileTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  onSelect,
  onDelete,
}: ProfileTableProps) {
  return (
    <section className="profile-table">
      <div className="profile-table__head">
        <h3 className="profile-table__title">Lista de perfis</h3>
        <span className="profile-table__meta">{items.length} registros</span>
      </div>

      <div className="profile-table__wrap">
        <table className="profile-table__table">
          <thead>
            <tr>
              <th>Perfil</th>
              <th>Descricao</th>
              <th>Nivel</th>
              <th>Criado em</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="profile-table__empty">
                  Carregando perfis...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="profile-table__empty">
                  Nenhum perfil encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.nome}-${item.nivelAcesso}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    <div className="profile-table__identity">
                      <strong>{item.nome || "-"}</strong>
                      <span>ID #{item.id ?? "-"}</span>
                    </div>
                  </td>
                  <td>{item.descricao || "Sem descricao"}</td>
                  <td>
                    <span className="profile-table__badge">
                      Nivel {item.nivelAcesso || "1"}
                    </span>
                  </td>
                  <td>{item.createdAt ? item.createdAt.slice(0, 10) : "-"}</td>
                  <td className="profile-table__actions">
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
                      <span className="profile-table__empty-action">Sem acoes</span>
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
