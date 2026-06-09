import type { ChartOfAccount } from "../../pages/Fi/ChartOfAccountsPage";

type ParentOption = {
  id: number;
  label: string;
};

type ChartOfAccountsFormProps = {
  value: ChartOfAccount;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  parentOptions: ParentOption[];
  onChange: (value: ChartOfAccount) => void;
  onSave: () => void;
  onReset: () => void;
};

const accountTypeOptions = ["analitica", "sintetica"];
const natureOptions = ["devedora", "credora"];
const statusOptions = ["ativo", "inativo"];

export default function ChartOfAccountsForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  parentOptions,
  onChange,
  onSave,
  onReset,
}: ChartOfAccountsFormProps) {
  const canSave =
    value.codigo.trim() !== "" &&
    value.nome.trim() !== "" &&
    value.tipoConta.trim() !== "" &&
    value.natureza.trim() !== "";

  function update<K extends keyof ChartOfAccount>(
    field: K,
    fieldValue: ChartOfAccount[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="chart-of-accounts-form">
      <div className="chart-of-accounts-form__head">
        <div>
          <h3 className="chart-of-accounts-form__title">
            {editing ? "Editar conta" : "Nova conta"}
          </h3>
          <p className="chart-of-accounts-form__subtitle">
            Defina a classificação contábil e a relação hierárquica da conta.
          </p>
          {editing && value.id ? (
            <p className="chart-of-accounts-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="chart-of-accounts-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="chart-of-accounts-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="chart-of-accounts-form__grid">
        <label className="chart-of-accounts-form__field">
          <span> Código </span>
          <input
            value={value.codigo}
            onChange={(event) => update("codigo", event.target.value)}
            placeholder="1.1.01"
            disabled={!canEditFields}
          />
        </label>

        <label className="chart-of-accounts-form__field">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Caixa e equivalentes"
            disabled={!canEditFields}
          />
        </label>

        <label className="chart-of-accounts-form__field">
          <span>Tipo de conta</span>
          <select
            value={value.tipoConta}
            onChange={(event) => update("tipoConta", event.target.value)}
            disabled={!canEditFields}
          >
            {accountTypeOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </label>

        <label className="chart-of-accounts-form__field">
          <span>Natureza</span>
          <select
            value={value.natureza}
            onChange={(event) => update("natureza", event.target.value)}
            disabled={!canEditFields}
          >
            {natureOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </label>

        <label className="chart-of-accounts-form__field">
          <span>Conta pai</span>
          {parentOptions.length > 0 ? (
            <select
              value={value.contaPai}
              onChange={(event) => update("contaPai", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Sem conta pai</option>
              {parentOptions.map((option) => (
                <option key={option.id} value={String(option.id)}>
                  {option.label}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.contaPai}
              onChange={(event) =>
                update("contaPai", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID da conta pai"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="chart-of-accounts-form__field">
          <span>Situação</span>
          <select
            value={value.situacao}
            onChange={(event) => update("situacao", event.target.value)}
            disabled={!canEditFields}
          >
            {statusOptions.map((option) => (
              <option key={option} value={option}>
                {option}
              </option>
            ))}
          </select>
        </label>
      </div>

      <button
        type="button"
        className="chart-of-accounts-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving ? "Salvando..." : editing ? "Salvar alteracoes" : "Criar conta"}
      </button>
    </aside>
  );
}
