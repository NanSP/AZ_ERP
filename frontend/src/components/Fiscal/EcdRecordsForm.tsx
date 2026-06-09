import type { EcdRecordEntry } from "../../pages/Fiscal/EcdRecordsPage";

type EcdRecordsFormProps = {
  value: EcdRecordEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: EcdRecordEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function EcdRecordsForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: EcdRecordsFormProps) {
  const canSave =
    value.periodo.trim() !== "" &&
    value.registro.trim() !== "" &&
    value.conteudo.trim() !== "";

  function update<K extends keyof EcdRecordEntry>(
    field: K,
    fieldValue: EcdRecordEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="ecd-records-form">
      <div className="ecd-records-page__panel-head">
        <div>
          <h3 className="ecd-records-page__panel-title">
            {editing ? "Editar registro ECD" : "Novo registro ECD"}
          </h3>
          <p className="ecd-records-page__panel-subtitle">
            Registre blocos ECD com periodo, codigo estruturado e conteudo
            contábil em JSON.
          </p>
          {editing && value.id ? (
            <p className="ecd-records-page__panel-meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="ecd-records-page__panel-meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="ecd-records-page__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="ecd-records-page__form-grid">
        <label className="ecd-records-page__field">
          <span>Periodo</span>
          <input
            type="date"
            value={value.periodo}
            onChange={(event) => update("periodo", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="ecd-records-page__field">
          <span>Registro</span>
          <input
            value={value.registro}
            onChange={(event) =>
              update(
                "registro",
                event.target.value.toUpperCase().replace(/[^A-Z0-9]/g, ""),
              )
            }
            placeholder="Ex.: I200"
            maxLength={4}
            disabled={!canEditFields}
          />
        </label>

        <label className="ecd-records-page__field ecd-records-page__field--span-2">
          <span>Conteudo (JSON)</span>
          <textarea
            value={value.conteudo}
            onChange={(event) => update("conteudo", event.target.value)}
            placeholder='{"campo":"valor","lancamentos":[1,2,3]}'
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="ecd-records-page__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar registro"}
      </button>
    </aside>
  );
}
