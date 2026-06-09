import type { SessionEntry, UserOption } from "../../pages/Portal/SessionsPage";

type SessionsTableProps = {
  items: SessionEntry[];
  loading: boolean;
  selectedId?: number;
  canEdit: boolean;
  userOptions: UserOption[];
  onSelect: (item: SessionEntry) => void;
};

function resolveUserLabel(userId: string, userOptions: UserOption[]) {
  const match = userOptions.find((option) => String(option.id) === userId);
  return match ? match.label : userId ? `Usuario #${userId}` : "-";
}

export default function SessionsTable({
  items,
  loading,
  selectedId,
  canEdit,
  userOptions,
  onSelect,
}: SessionsTableProps) {
  return (
    <section className="sessions-table">
      <div className="sessions-table__head">
        <h3 className="sessions-table__title">Sessões</h3>
        <span className="sessions-table__meta">{items.length} registros</span>
      </div>

      <div className="sessions-table__wrap">
        <table className="sessions-table__table">
          <thead>
            <tr>
              <th>Usuário</th>
              <th>Contexto</th>
              <th>Ciclo</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>

          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} className="sessions-table__empty">
                  Carregando sessões...
                </td>
              </tr>
            ) : items.length === 0 ? (
              <tr>
                <td colSpan={5} className="sessions-table__empty">
                  Nenhuma sessão encontrada.
                </td>
              </tr>
            ) : (
              items.map((item) => (
                <tr
                  key={item.id ?? item.tokenSessao}
                  data-selected={item.id === selectedId ? "true" : "false"}
                >
                  <td>{resolveUserLabel(item.usuario, userOptions)}</td>
                  <td>
                    <div className="sessions-table__identity">
                      <strong>{item.ipAddress || "-"}</strong>
                      <span>{item.userAgent || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <div className="sessions-table__details">
                      <span>Login: {item.dataLogin || "-"}</span>
                      <span>Expira: {item.expiracao || "-"}</span>
                    </div>
                  </td>
                  <td>
                    <span
                      className={
                        item.dataLogout
                          ? "sessions-table__badge"
                          : "sessions-table__badge sessions-table__badge--active"
                      }
                    >
                      {item.dataLogout ? "Encerrada" : "Ativa"}
                    </span>
                  </td>
                  <td className="sessions-table__actions">
                    {canEdit ? (
                      <button type="button" onClick={() => onSelect(item)}>
                        Editar
                      </button>
                    ) : (
                      <span className="sessions-table__empty-action">
                        Sem ações
                      </span>
                    )}
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
