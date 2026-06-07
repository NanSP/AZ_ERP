import type { Device, UserOption } from "../../pages/Portal/DevicesPage";

type DevicesTableProps = {
  items: Device[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  canDelete: boolean;
  canDeleteItem: (item: Device) => boolean;
  userOptions: UserOption[];
  onSelect: (item: Device) => void;
  onDelete: (item: Device) => void;
};

function resolveUserLabel(userId: string, userOptions: UserOption[]) {
  const match = userOptions.find((option) => String(option.id) === userId);
  return match ? match.label : userId ? `Usuario #${userId}` : "-";
}

export default function DevicesTable({
  items,
  loading,
  selectedId,
  canEdit,
  canDelete,
  canDeleteItem,
  userOptions,
  onSelect,
  onDelete,
}: DevicesTableProps) {
  return (
    <section className="devices-table">
      <div className="devices-table__head">
        <h3 className="devices-table__title">Dispositivos</h3>
        <span className="devices-table__meta">{items.length} registros</span>
      </div>

      <div className="devices-table__wrap">
        <table className="devices-table__table">
          <thead>
            <tr>
              <th>Usuario</th>
              <th>Dispositivo</th>
              <th>Push e acesso</th>
              <th>Status</th>
              <th>Acoes</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="devices-table__empty">
                  Carregando dispositivos...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="devices-table__empty">
                  Nenhum dispositivo encontrado.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.deviceId}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{resolveUserLabel(item.usuario, userOptions)}</td>
                  <td>
                    <div className="devices-table__identity">
                      <strong>{item.deviceId || "-"}</strong>
                      <span>{item.deviceModel || item.devicePlatform || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="devices-table__details">
                      <span>{item.pushToken || "-"}</span>
                      <span>{item.ultimoAcesso || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.ativo
                          ? "devices-table__badge devices-table__badge--active"
                          : "devices-table__badge"
                      }
                    >
                      {item.ativo ? "Ativo" : "Inativo"}
                    </span>
                  </td>
                  <td className="devices-table__actions">
                    {canEdit ? (
                      <button type="button" onClick={() => onSelect(item)}>
                        Editar
                      </button>
                    ) : null}
                    {canDelete ? (
                      <button
                        type="button"
                        onClick={() => onDelete(item)}
                        disabled={!canDeleteItem(item)}
                        title={
                          canDeleteItem(item)
                            ? undefined
                            : item.ultimoAcesso.trim() !== ""
                              ? "Dispositivos com historico de acesso nao podem ser excluidos."
                              : "Dispositivos ativos devem ser desativados antes da exclusao."
                        }
                      >
                        Excluir
                      </button>
                    ) : null}
                    {!canEdit && !canDelete ? (
                      <span className="devices-table__empty-action">
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
