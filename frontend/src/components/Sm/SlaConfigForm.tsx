import type { SlaConfigEntry } from "../../pages/Sm/SlaConfigPage";

type SlaConfigFormProps = {
  value: SlaConfigEntry;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: SlaConfigEntry) => void;
  onSave: () => void;
  onReset: () => void;
};

const priorityOptions = [
  { value: "baixa", label: "Baixa" },
  { value: "normal", label: "Normal" },
  { value: "alta", label: "Alta" },
  { value: "critica", label: "Critica" },
];

export default function SlaConfigForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: SlaConfigFormProps) {
  const canSave = value.tipoServico.trim() !== "";

  function update<K extends keyof SlaConfigEntry>(
    field: K,
    fieldValue: SlaConfigEntry[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="sla-form">
      <div className="sla-form__head">
        <div>
          <h3 className="sla-form__title">
            {editing ? "Editar SLA" : "Novo SLA"}
          </h3>
          <p className="sla-form__subtitle">
            Relacione prioridade e tempos esperados ao tipo de servico.
          </p>
          {editing && value.id ? (
            <p className="sla-form__meta">Registro selecionado: #{value.id}</p>
          ) : !canEditFields ? (
            <p className="sla-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="sla-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="sla-form__grid">
        <label className="sla-form__field sla-form__field--span-2">
          <span>Tipo de servico</span>
          <input
            value={value.tipoServico}
            onChange={(event) => update("tipoServico", event.target.value)}
            placeholder="instalacao, manutencao, visita..."
            disabled={!canEditFields}
          />
        </label>

        <label className="sla-form__field">
          <span>Prioridade</span>
          <select
            value={value.prioridade}
            onChange={(event) => update("prioridade", event.target.value)}
            disabled={!canEditFields}
          >
            {priorityOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="sla-form__field">
          <span>Tempo de atendimento (h)</span>
          <input
            value={value.tempoAtendimentoHoras}
            onChange={(event) =>
              update(
                "tempoAtendimentoHoras",
                event.target.value.replace(/\D/g, ""),
              )
            }
            placeholder="4"
            disabled={!canEditFields}
          />
        </label>

        <label className="sla-form__field">
          <span>Tempo de resolucao (h)</span>
          <input
            value={value.tempoResolucaoHoras}
            onChange={(event) =>
              update(
                "tempoResolucaoHoras",
                event.target.value.replace(/\D/g, ""),
              )
            }
            placeholder="24"
            disabled={!canEditFields}
          />
        </label>
      </div>

      <button
        type="button"
        className="sla-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar configuracao"}
      </button>
    </aside>
  );
}
