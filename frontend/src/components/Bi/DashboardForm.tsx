import type { DashboardRecord } from "../../pages/Bi/DashboardsPage";

type DashboardFormProps = {
  value: DashboardRecord;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: DashboardRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function DashboardForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: DashboardFormProps) {
  const canSave = value.nome.trim() !== "";

  function update<K extends keyof DashboardRecord>(
    field: K,
    fieldValue: DashboardRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="dashboard-form">
      <div className="dashboard-form__head">
        <div>
          <h3 className="dashboard-form__title">
            {editing ? "Editar dashboard" : "Novo dashboard"}
          </h3>
          <p className="dashboard-form__subtitle">
            Configure nome, descricao, layout e parametros do painel.
          </p>
          {editing && value.id ? (
            <p className="dashboard-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="dashboard-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="dashboard-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="dashboard-form__grid">
        <label className="dashboard-form__field">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Painel executivo"
            disabled={!canEditFields}
          />
        </label>

        <label className="dashboard-form__field dashboard-form__field--span-2">
          <span>Descricao</span>
          <input
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Resumo do objetivo e do publico do dashboard"
            disabled={!canEditFields}
          />
        </label>

        <label className="dashboard-form__field dashboard-form__field--span-2">
          <span>Layout (JSON)</span>
          <textarea
            value={value.layout}
            onChange={(event) => update("layout", event.target.value)}
            placeholder='{"widgets":[]}'
            disabled={!canEditFields}
            rows={9}
          />
        </label>

        <label className="dashboard-form__field dashboard-form__field--span-2">
          <span>Configuracoes (JSON)</span>
          <textarea
            value={value.configuracoes}
            onChange={(event) => update("configuracoes", event.target.value)}
            placeholder='{"tema":"corporativo"}'
            disabled={!canEditFields}
            rows={9}
          />
        </label>
      </div>

      <button
        type="button"
        className="dashboard-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar dashboard"}
      </button>
    </aside>
  );
}
