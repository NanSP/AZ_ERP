import type { ProfilePermissionAssignment } from "../../pages/Sys/ProfilePermissionsPage";

type RelatedOption = {
  id: number;
  label: string;
};

type ProfilePermissionTableProps = {
  items: ProfilePermissionAssignment[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  profileOptions: RelatedOption[];
  permissionOptions: RelatedOption[];
  onSelect: (item: ProfilePermissionAssignment) => void;
  onDelete: (item: ProfilePermissionAssignment) => void;
};

function resolveLabel(
  value: string,
  options: RelatedOption[],
  fallback: string,
) {
  const id = Number(value);
  const match = options.find((option) => option.id === id);
  return match?.label ?? fallback;
}

export default function ProfilePermissionTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  profileOptions,
  permissionOptions,
  onSelect,
  onDelete,
}: ProfilePermissionTableProps) {
  return (
    <section className="profile-permission-table">
      <div className="profile-permission-table__head">
        <h3 className="profile-permission-table__title">
          Lista de vinculações
        </h3>
        <span className="profile-permission-table__meta">
          {items.length} registros
        </span>
      </div>

      <div className="profile-permission-table__wrap">
        <table className="profile-permission-table__table">
          <thead>
            <tr>
              <th>Perfil</th>
              <th>Permissão</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={3} className="profile-permission-table__empty">
                  Carregando vinculações...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={3} className="profile-permission-table__empty">
                  Nenhuma vinculação encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? `${item.perfil}-${item.permissao}`}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>
                    {resolveLabel(
                      item.perfil,
                      profileOptions,
                      `Perfil #${item.perfil || "-"}`,
                    )}
                  </td>
                  <td>
                    {resolveLabel(
                      item.permissao,
                      permissionOptions,
                      `Permissão #${item.permissao || "-"}`,
                    )}
                  </td>
                  <td className="profile-permission-table__actions">
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
                      <span className="profile-permission-table__empty-action">
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
