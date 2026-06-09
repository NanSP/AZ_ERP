import type {
  EmployeeAccess,
  EmployeeOption,
  PayrollEntry,
} from "../../pages/Rh/PayrollPage";

type PayrollFormProps = {
  value: PayrollEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  employeeOptions: EmployeeOption[];
  employeeAccess: EmployeeAccess;
  onChange: (value: PayrollEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "calculado", label: "Calculado" },
  { value: "pago", label: "Pago" },
  { value: "cancelado", label: "Cancelado" },
];

export default function PayrollForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  employeeOptions,
  employeeAccess,
  onChange,
  onSave,
  onReset,
}: PayrollFormProps) {
  const canSave =
    value.colaborador.trim() !== "" &&
    value.competencia.trim() !== "" &&
    !(value.status === "pago" && value.dataPagamento.trim() === "");

  function update<K extends keyof PayrollEntry>(
    field: K,
    fieldValue: PayrollEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="payroll-form">
      <div className="payroll-form__head">
        <div>
          <h3 className="payroll-form__title">
            {editing ? "Editar folha" : "Nova folha"}
          </h3>
          <p className="payroll-form__subtitle">
            Informe a competência e os valores. Horas podem ser herdadas do
            ponto quando deixadas em branco.
          </p>
          {editing && value.id ? (
            <p className="payroll-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="payroll-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="payroll-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="payroll-form__grid">
        <label className="payroll-form__field">
          <span>Colaborador</span>
          {employeeOptions.length > 0 ? (
            <select
              value={value.colaborador}
              onChange={(event) => update("colaborador", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um colaborador</option>
              {employeeOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.colaborador}
              onChange={(event) =>
                update("colaborador", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do colaborador"
              disabled={!canEditFields}
            />
          )}
          {employeeAccess === "unavailable" ? (
            <small className="payroll-form__hint">
              Lista indisponível. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="payroll-form__field">
          <span>Competência</span>
          <input
            type="date"
            value={value.competencia}
            onChange={(event) => update("competencia", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="payroll-form__field">
          <span>Salário base</span>
          <input
            value={value.salarioBase}
            onChange={(event) =>
              update("salarioBase", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="payroll-form__field">
          <span>Horas normais</span>
          <input
            value={value.horasNormais}
            onChange={(event) =>
              update(
                "horasNormais",
                event.target.value.replace(/[^0-9.,]/g, ""),
              )
            }
            placeholder="Vazio = calcular pelo ponto"
            disabled={!canEditFields}
          />
        </label>

        <label className="payroll-form__field">
          <span>Horas extras</span>
          <input
            value={value.horasExtras}
            onChange={(event) =>
              update("horasExtras", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="Vazio = calcular pelo ponto"
            disabled={!canEditFields}
          />
        </label>

        <label className="payroll-form__field">
          <span>Adicionais</span>
          <input
            value={value.adicionais}
            onChange={(event) =>
              update("adicionais", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="payroll-form__field">
          <span>Descontos</span>
          <input
            value={value.descontos}
            onChange={(event) =>
              update("descontos", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="payroll-form__field">
          <span>Data de pagamento</span>
          <input
            type="date"
            value={value.dataPagamento}
            onChange={(event) => update("dataPagamento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="payroll-form__field">
          <span>Status</span>
          <select
            value={value.status}
            onChange={(event) => update("status", event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <div className="payroll-form__summary">
          <span>Valor hora: {value.valorHora || "0"}</span>
          <span>Horas normais: {value.valorHorasNormais || "0"}</span>
          <span>Horas extras: {value.valorHorasExtras || "0"}</span>
          <span>Bruto: {value.valorBruto || "0"}</span>
          <span>Líquido: {value.valorLiquido || "0"}</span>
        </div>
      </div>

      <button
        type="button"
        className="payroll-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving ? "Salvando..." : editing ? "Salvar alterações" : "Criar folha"}
      </button>
    </aside>
  );
}
