import type { CostCenter } from "../../pages/Fi/CostCentersPage";

type CostCenterFormProps = {
  value: CostCenter;
  editing: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: CostCenter) => void;
  onSave: () => void;
  onReset: () => void;
};

export default function CostCenterForm({
  value,
  editing,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: CostCenterFormProps) {
  const canSave =
    value.codigo.trim() !== "" &&
    value.nome.trim() !== "";

  function update<K extends keyof CostCenter>(
    field: K,
    fieldValue: CostCenter[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="cost-center-form">
      <div className="cost-center-form__head">
        <div>
          <h3 className="cost-center-form__title">
            {editing ? "Editar centro" : "Novo centro"}
          </h3>
          <p className="cost-center-form__subtitle">
            Cadastre a estrutura base para rateio e classificacao financeira.
          </p>
          {editing && value.id ? (
            <p className="cost-center-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="cost-center-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="cost-center-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="cost-center-form__grid">
        <label className="cost-center-form__field">
          <span>Codigo</span>
          <input
            value={value.codigo}
            onChange={(event) => update("codigo", event.target.value)}
            placeholder="CC-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="cost-center-form__field">
          <span>Nome</span>
          <input
            value={value.nome}
            onChange={(event) => update("nome", event.target.value)}
            placeholder="Centro administrativo"
            disabled={!canEditFields}
          />
        </label>

        <label className="cost-center-form__field">
          <span>Tipo</span>
          <input
            value={value.tipo}
            onChange={(event) => update("tipo", event.target.value)}
            placeholder="Operacional, administrativo, comercial..."
            disabled={!canEditFields}
          />
        </label>

        <label className="cost-center-form__field">
          <span>Responsavel</span>
          <input
            value={value.responsavel}
            onChange={(event) => update("responsavel", event.target.value)}
            placeholder="Nome do responsavel"
            disabled={!canEditFields}
          />
        </label>

        <label className="cost-center-form__checkbox">
          <input
            type="checkbox"
            checked={value.ativo}
            onChange={(event) => update("ativo", event.target.checked)}
            disabled={!canEditFields}
          />
          <span>Centro ativo</span>
        </label>
      </div>

      <button
        type="button"
        className="cost-center-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar centro"}
      </button>
    </aside>
  );
}
