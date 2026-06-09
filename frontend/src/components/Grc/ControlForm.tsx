import type { User } from "../../pages/Sys/UsersPage";
import type { ControlRecord } from "../../pages/Grc/ControlsPage";

type ControlFormProps = {
  value: ControlRecord;
  editing: boolean;
  users: User[];
  canReadUsers: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: ControlRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const tipoControleOptions = [
  { value: "preventivo", label: "Preventivo" },
  { value: "detectivo", label: "Detectivo" },
  { value: "corretivo", label: "Corretivo" },
];

const frequenciaOptions = [
  { value: "diaria", label: "Diaria" },
  { value: "semanal", label: "Semanal" },
  { value: "mensal", label: "Mensal" },
  { value: "trimestral", label: "Trimestral" },
  { value: "anual", label: "Anual" },
  { value: "sob_demanda", label: "Sob demanda" },
];

export default function ControlForm({
  value,
  editing,
  users,
  canReadUsers,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: ControlFormProps) {
  const requiresResponsible = value.codigo.trim() !== "" || value.efetivo;
  const canSave =
    value.descricao.trim() !== "" &&
    value.tipoControle.trim() !== "" &&
    value.frequencia.trim() !== "" &&
    (!requiresResponsible || value.responsavelId.trim() !== "");

  function update<K extends keyof ControlRecord>(
    field: K,
    fieldValue: ControlRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="control-form">
      <div className="control-form__head">
        <div>
          <h3 className="control-form__title">
            {editing ? "Editar controle" : "Novo controle"}
          </h3>
          <p className="control-form__subtitle">
            Estruture o controle, defina frequência e formalize a
            responsabilização quando ele entrar em ciclo de governança.
          </p>
          {editing && value.id ? (
            <p className="control-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="control-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="control-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="control-form__grid">
        <label className="control-form__field">
          <span>Código</span>
          <input
            value={value.codigo}
            onChange={(event) => update("codigo", event.target.value)}
            placeholder="CTRL-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="control-form__field">
          <span>Tipo de controle</span>
          <select
            value={value.tipoControle}
            onChange={(event) => update("tipoControle", event.target.value)}
            disabled={!canEditFields}
          >
            {tipoControleOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="control-form__field control-form__field--span-2">
          <span>Descrição</span>
          <textarea
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descreva objetivo, execução e evidência esperada"
            disabled={!canEditFields}
            rows={4}
          />
        </label>

        <label className="control-form__field">
          <span>Frequência</span>
          <select
            value={value.frequencia}
            onChange={(event) => update("frequencia", event.target.value)}
            disabled={!canEditFields}
          >
            {frequenciaOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="control-form__field">
          <span>Responsável</span>
          {canReadUsers ? (
            <select
              value={value.responsavelId}
              onChange={(event) => update("responsavelId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Sem responsável</option>
              {users.map((user) => (
                <option
                  key={user.id ?? user.login}
                  value={String(user.id ?? "")}
                >
                  {user.nome || user.login}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.responsavelId}
              onChange={(event) =>
                update("responsavelId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do responsável"
              disabled={!canEditFields}
            />
          )}
          {!canReadUsers ? (
            <small className="control-form__hint">
              Sem leitura de usuarios: informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="control-form__checkbox">
          <input
            type="checkbox"
            checked={value.efetivo}
            onChange={(event) => update("efetivo", event.target.checked)}
            disabled={!canEditFields}
          />
          <span>Controle efetivo</span>
        </label>

        {requiresResponsible ? (
          <p className="control-form__hint control-form__hint--span-2">
            Controle com código ou marcado como efetivo exige responsável.
          </p>
        ) : null}
      </div>

      <button
        type="button"
        className="control-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alterações"
            : "Criar controle"}
      </button>
    </aside>
  );
}
