import type {
  Benefit,
  EmployeeAccess,
  EmployeeOption,
} from "../../pages/Rh/BenefitsPage";

type BenefitFormProps = {
  value: Benefit;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  employeeOptions: EmployeeOption[];
  employeeAccess: EmployeeAccess;
  onChange: (value: Benefit) => void;
  onSave: () => void;
  onReset: () => void;
};

const benefitTypeOptions = [
  { value: "", label: "Selecione" },
  { value: "vale_transporte", label: "Vale-transporte" },
  { value: "vale_refeicao", label: "Vale-refeicao" },
  { value: "plano_saude", label: "Plano de saude" },
  { value: "plano_odontologico", label: "Plano odontologico" },
];

export default function BenefitForm({
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
}: BenefitFormProps) {
  const canSave =
    value.colaborador.trim() !== "" &&
    value.tipoBeneficio.trim() !== "" &&
    (!value.ativo || value.dataInicio.trim() !== "");

  function update<K extends keyof Benefit>(field: K, fieldValue: Benefit[K]) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="benefit-form">
      <div className="benefit-form__head">
        <div>
          <h3 className="benefit-form__title">
            {editing ? "Editar beneficio" : "Novo beneficio"}
          </h3>
          <p className="benefit-form__subtitle">
            Configure o beneficio do colaborador com vigencia, valor e status.
          </p>
          {editing && value.id ? (
            <p className="benefit-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="benefit-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="benefit-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="benefit-form__grid">
        <label className="benefit-form__field">
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
            <small className="benefit-form__hint">
              Lista indisponivel. Informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="benefit-form__field">
          <span>Tipo de beneficio</span>
          <select
            value={value.tipoBeneficio}
            onChange={(event) => update("tipoBeneficio", event.target.value)}
            disabled={!canEditFields}
          >
            {benefitTypeOptions.map((option) => (
              <option key={option.value || "empty"} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="benefit-form__field">
          <span>Valor</span>
          <input
            value={value.valor}
            onChange={(event) =>
              update("valor", event.target.value.replace(/[^0-9.,]/g, ""))
            }
            placeholder="0.00"
            disabled={!canEditFields}
          />
        </label>

        <label className="benefit-form__field">
          <span>Data de inicio</span>
          <input
            type="date"
            value={value.dataInicio}
            onChange={(event) => update("dataInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="benefit-form__field">
          <span>Data de fim</span>
          <input
            type="date"
            value={value.dataFim}
            onChange={(event) => update("dataFim", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="benefit-form__checkbox">
          <input
            type="checkbox"
            checked={value.ativo}
            onChange={(event) => update("ativo", event.target.checked)}
            disabled={!canEditFields}
          />
          <span>Beneficio ativo</span>
        </label>
      </div>

      <button
        type="button"
        className="benefit-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar beneficio"}
      </button>
    </aside>
  );
}
