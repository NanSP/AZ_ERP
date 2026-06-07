import type { EfdRecordEntry } from "../../pages/Fiscal/EfdRecordsPage";

type EfdRecordsFormProps = {
  value: EfdRecordEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: EfdRecordEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function EfdRecordsForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: EfdRecordsFormProps) {
  const canSave =
    value.periodo.trim() !== "" &&
    value.registro.trim() !== "" &&
    value.conteudo.trim() !== "";

  function update<K extends keyof EfdRecordEntry>(
    field: K,
    fieldValue: EfdRecordEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="efd-records-form">
      <div className="efd-records-page__panel-head">
        <div>
          <h3 className="efd-records-page__panel-title">
            {editing ? "Editar registro EFD" : "Novo registro EFD"}
          </h3>
          <p className="efd-records-page__panel-subtitle">
            Registre blocos EFD com periodo, codigo do registro e conteudo estruturado em JSON.
          </p>
          {editing && value.id ? (
            <p className="efd-records-page__panel-meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="efd-records-page__panel-meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="efd-records-page__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="efd-records-page__form-grid">
        <label className="efd-records-page__field">
          <span>Periodo</span>
          <input
            type="date"
            value={value.periodo}
            onChange={(event) => update("periodo", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="efd-records-page__field">
          <span>Registro</span>
          <input
            value={value.registro}
            onChange={(event) =>
              update(
                "registro",
                event.target.value.toUpperCase().replace(/[^A-Z0-9]/g, ""),
              )
            }
            placeholder="Ex.: C100"
            maxLength={4}
            disabled={!canEditFields}
          />
        </label>

        <label className="efd-records-page__field efd-records-page__field--span-2">
          <span>Conteudo (JSON)</span>
          <textarea
            value={value.conteudo}
            onChange={(event) => update("conteudo", event.target.value)}
            placeholder='{"campo":"valor","itens":[1,2,3]}'
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="efd-records-page__button"
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
