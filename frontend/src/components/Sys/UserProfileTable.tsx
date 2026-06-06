import type {
  UserProfileAssignment,
} from "../../pages/Sys/UserProfilesPage";

type RelatedOption = {
  id: number;
  label: string;
};

type UserProfileTableProps = {
  items: UserProfileAssignment[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  userOptions: RelatedOption[];
  profileOptions: RelatedOption[];
  onSelect: (item: UserProfileAssignment) => void;
  onDelete: (item: UserProfileAssignment) => void;
};

function resolveLabel(value: string, options: RelatedOption[], fallback: string) {
  const id = Number(value);
  const match = options.find((option) => option.id === id);
  return match?.label ?? fallback;
}

export default function UserProfileTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  userOptions,
  profileOptions,
  onSelect,
  onDelete,
}: UserProfileTableProps) {
  return (
    <section className="user-profile-table">
      <div className="user-profile-table__head">
        <h3 className="user-profile-table__title">Lista de vinculacoes</h3>
        <span className="user-profile-table__meta">{items.length} registros</span>
      </div>

      <div className="user-profile-table__wrap">
        <table className="user-profile-table__table">
          <thead>
            <tr>
              <th>Usuario</th>
              <th>Perfil</th>
              <th>Atribuido em</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} className="user-profile-table__empty">
                  Carregando vinculacoes...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={4} className="user-profile-table__empty">
                  Nenhuma vinculacao encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.usuario}-${item.perfil}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    {resolveLabel(
                      item.usuario,
                      userOptions,
                      `Usuario #${item.usuario || "-"}`,
                    )}
                  </td>
                  <td>
                    {resolveLabel(
                      item.perfil,
                      profileOptions,
                      `Perfil #${item.perfil || "-"}`,
                    )}
                  </td>
                  <td>{item.dataAtribuicao ? item.dataAtribuicao.slice(0, 10) : "-"}</td>
                  <td className="user-profile-table__actions">
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
                      <span className="user-profile-table__empty-action">
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
