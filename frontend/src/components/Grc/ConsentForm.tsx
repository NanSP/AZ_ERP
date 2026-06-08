import type { ConsentRecord } from "../../pages/Grc/ConsentsPage";

type ConsentFormProps = {
  value: ConsentRecord;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: ConsentRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const tipoTitularOptions = [
  { value: "cliente", label: "Cliente" },
  { value: "colaborador", label: "Colaborador" },
  { value: "fornecedor", label: "Fornecedor" },
  { value: "usuario", label: "Usuario" },
  { value: "outro", label: "Outro" },
];

export default function ConsentForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: ConsentFormProps) {
  const revogacaoAfterConsentimento =
    value.dataConsentimento.trim() === "" ||
    value.dataRevogacao.trim() === "" ||
    value.dataRevogacao >= value.dataConsentimento;
  const canSave =
    value.titular.trim() !== "" &&
    value.tipoTitular.trim() !== "" &&
    value.finalidade.trim() !== "" &&
    revogacaoAfterConsentimento;

  function update<K extends keyof ConsentRecord>(
    field: K,
    fieldValue: ConsentRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="consent-form">
      <div className="consent-form__head">
        <div>
          <h3 className="consent-form__title">
            {editing ? "Editar consentimento" : "Novo consentimento"}
          </h3>
          <p className="consent-form__subtitle">
            Registre o titular, a finalidade e a trilha de consentimento e revogacao.
          </p>
          {editing && value.id ? (
            <p className="consent-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="consent-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="consent-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="consent-form__grid">
        <label className="consent-form__field">
          <span>ID do titular</span>
          <input
            value={value.titular}
            onChange={(event) =>
              update("titular", event.target.value.replace(/\D/g, ""))
            }
            placeholder="1"
            disabled={!canEditFields}
          />
        </label>

        <label className="consent-form__field">
          <span>Tipo do titular</span>
          <select
            value={value.tipoTitular}
            onChange={(event) => update("tipoTitular", event.target.value)}
            disabled={!canEditFields}
          >
            {tipoTitularOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="consent-form__field consent-form__field--span-2">
          <span>Finalidade</span>
          <input
            value={value.finalidade}
            onChange={(event) => update("finalidade", event.target.value)}
            placeholder="Comunicacao comercial e operacional"
            disabled={!canEditFields}
          />
        </label>

        <label className="consent-form__field">
          <span>Data do consentimento</span>
          <input
            type="datetime-local"
            value={value.dataConsentimento}
            onChange={(event) => update("dataConsentimento", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="consent-form__field">
          <span>Data da revogacao</span>
          <input
            type="datetime-local"
            value={value.dataRevogacao}
            onChange={(event) => update("dataRevogacao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="consent-form__field">
          <span>IP de origem</span>
          <input
            value={value.ipAddress}
            onChange={(event) => update("ipAddress", event.target.value)}
            placeholder="127.0.0.1"
            disabled={!canEditFields}
          />
        </label>

        <label className="consent-form__field">
          <span>User agent</span>
          <input
            value={value.userAgent}
            onChange={(event) => update("userAgent", event.target.value)}
            placeholder="Mozilla/5.0 ..."
            disabled={!canEditFields}
          />
        </label>

        {!revogacaoAfterConsentimento ? (
          <p className="consent-form__hint consent-form__hint--span-2">
            Data de revogacao nao pode ser anterior a data de consentimento.
          </p>
        ) : null}
      </div>

      <button
        type="button"
        className="consent-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar consentimento"}
      </button>
    </aside>
  );
}
