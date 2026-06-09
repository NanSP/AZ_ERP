import type {
  Attendance,
  EmployeeOption,
  OrderOption,
  RelatedAccess,
} from "../../pages/Sm/AttendancesPage";

type AttendanceFormProps = {
  value: Attendance;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  orderOptions: OrderOption[];
  orderAccess: RelatedAccess;
  employeeOptions: EmployeeOption[];
  employeeAccess: RelatedAccess;
  onChange: (value: Attendance) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function AttendanceForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  orderOptions,
  orderAccess,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: AttendanceFormProps) {
  const canSave = value.os.trim() !== "" && value.tecnico.trim() !== "";

  function update<K extends keyof Attendance>(
    field: K,
    fieldValue: Attendance[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="attendance-form">
      <div className="attendance-form__head">
        <div>
          <h3 className="attendance-form__title">
            {editing ? "Editar atendimento" : "Novo atendimento"}
          </h3>
          <p className="attendance-form__subtitle">
            Registre a execução técnica da ordem, horas gastas e materiais
            aplicados.
          </p>
          {editing && value.id ? (
            <p className="attendance-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="attendance-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="attendance-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="attendance-form__grid">
        <label className="attendance-form__field">
          <span>Ordem de serviço</span>
          {orderOptions.length > 0 ? (
            <select
              value={value.os}
              onChange={(event) => update("os", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {orderOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.os}
              onChange={(event) =>
                update("os", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da OS"
              disabled={!canEditFields}
            />
          )}
          {orderAccess === "unavailable" ? (
            <small className="attendance-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="attendance-form__field">
          <span>Técnico</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.tecnico}
              onChange={(event) => update("tecnico", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.tecnico}
              onChange={(event) =>
                update("tecnico", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do técnico"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="attendance-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="attendance-form__field">
          <span>Data e hora</span>
          <input
            type="datetime-local"
            value={value.dataHora}
            onChange={(event) => update("dataHora", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="attendance-form__field">
          <span>Horas gastas</span>
          <input
            value={value.horasGastas}
            onChange={(event) =>
              update("horasGastas", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="attendance-form__field attendance-form__field--span-2">
          <span>Descrição</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descreva a execução do atendimento"
            disabled={!canEditFields}
          />
        </label>

        <label className="attendance-form__field attendance-form__field--span-2">
          <span>Materiais utilizados</span>
          <textarea
            value={value.materiaisUtilizados}
            onChange={(event) =>
              update("materiaisUtilizados", event.target.value)
            }
            placeholder={
              '{\n  "pecas": ["item-a"],\n  "observacao": "aplicado em campo"\n}'
            }
            disabled={!canEditFields}
          />
          <small className="attendance-form__hint">
            Informe um JSON valido quando houver materiais utilizados.
          </small>
        </label>
      </div>

      <button
        type="button"
        className="attendance-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar atendimento"}
      </button>
    </aside>
  );
}
