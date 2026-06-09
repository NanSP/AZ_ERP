import type { EsocialEventEntry } from "../../pages/Fiscal/EsocialEventsPage";

type EsocialEventsFormProps = {
  value: EsocialEventEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: EsocialEventEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

const statusOptions = [
  { value: "gerado", label: "Gerado" },
  { value: "enviado", label: "Enviado" },
  { value: "processado", label: "Processado" },
  { value: "erro", label: "Erro" },
];

export default function EsocialEventsForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: EsocialEventsFormProps) {
  const canSave =
    value.periodoApuracao.trim() !== "" &&
    value.tipoEvento.trim() !== "" &&
    value.conteudo.trim() !== "";

  function update<K extends keyof EsocialEventEntry>(
    field: K,
    fieldValue: EsocialEventEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="esocial-events-form">
      <div className="esocial-events-page__panel-head">
        <div>
          <h3 className="esocial-events-page__panel-title">
            {editing ? "Editar evento eSocial" : "Novo evento eSocial"}
          </h3>
          <p className="esocial-events-page__panel-subtitle">
            Registre eventos fiscais com periodo de apuracao, conteudo
            estruturado e fluxo de envio.
          </p>
          {editing && value.id ? (
            <p className="esocial-events-page__panel-meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="esocial-events-page__panel-meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="esocial-events-page__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="esocial-events-page__form-grid">
        <label className="esocial-events-page__field">
          <span>Periodo de apuração</span>
          <input
            type="date"
            value={value.periodoApuracao}
            onChange={(event) => update("periodoApuracao", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="esocial-events-page__field">
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

        <label className="esocial-events-page__field">
          <span>Tipo do evento</span>
          <input
            value={value.tipoEvento}
            onChange={(event) =>
              update("tipoEvento", event.target.value.toLowerCase())
            }
            placeholder="Ex.: s1200"
            disabled={!canEditFields}
          />
        </label>

        <label className="esocial-events-page__field">
          <span>Evento ID</span>
          <input
            value={value.eventoId}
            onChange={(event) => update("eventoId", event.target.value)}
            placeholder="Obrigatorio para enviado/processado"
            disabled={!canEditFields}
          />
        </label>

        <label className="esocial-events-page__field esocial-events-page__field--span-2">
          <span>Conteúdo</span>
          <textarea
            value={value.conteudo}
            onChange={(event) => update("conteudo", event.target.value)}
            placeholder="<evento>conteudo estruturado</evento>"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="esocial-events-page__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar evento"}
      </button>
    </aside>
  );
}
