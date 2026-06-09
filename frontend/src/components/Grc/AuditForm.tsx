import type { User } from "../../pages/Sys/UsersPage";
import type { AuditRecord } from "../../pages/Grc/AuditsPage";

type AuditFormProps = {
  value: AuditRecord;
  editing: boolean;
  users: User[];
  canReadUsers: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: AuditRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const tipoAuditoriaOptions = [
  { value: "interna", label: "Interna" },
  { value: "externa", label: "Externa" },
  { value: "regulatoria", label: "Regulatoria" },
];

const statusOptions = [
  { value: "planejada", label: "Planejada" },
  { value: "em_andamento", label: "Em andamento" },
  { value: "concluida", label: "Concluida" },
  { value: "cancelada", label: "Cancelada" },
];

export default function AuditForm({
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
}: AuditFormProps) {
  const requiresStartDate =
    value.status === "em_andamento" || value.status === "concluida";
  const requiresResponsible =
    value.status === "em_andamento" || value.status === "concluida";
  const requiresEndDate = value.status === "concluida";
  const plannedMustNotHaveEndDate = value.status === "planejada";
  const datesAreOrdered =
    value.dataInicio.trim() === "" ||
    value.dataFim.trim() === "" ||
    value.dataFim >= value.dataInicio;
  const canSave =
    value.titulo.trim() !== "" &&
    value.tipoAuditoria.trim() !== "" &&
    value.status.trim() !== "" &&
    (!requiresStartDate || value.dataInicio.trim() !== "") &&
    (!requiresEndDate || value.dataFim.trim() !== "") &&
    (!requiresResponsible || value.responsavelId.trim() !== "") &&
    (!plannedMustNotHaveEndDate || value.dataFim.trim() === "") &&
    datesAreOrdered;

  function update<K extends keyof AuditRecord>(
    field: K,
    fieldValue: AuditRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="audit-form">
      <div className="audit-form__head">
        <div>
          <h3 className="audit-form__title">
            {editing ? "Editar auditoria" : "Nova auditoria"}
          </h3>
          <p className="audit-form__subtitle">
            Configure o plano, a janela de execução e o responsavel da
            auditoria.
          </p>
          {editing && value.id ? (
            <p className="audit-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="audit-form__meta">
              Seu perfil possui acesso limitado para alterações neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="audit-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="audit-form__grid">
        <label className="audit-form__field audit-form__field--span-2">
          <span>Título</span>
          <input
            value={value.titulo}
            onChange={(event) => update("titulo", event.target.value)}
            placeholder="Auditoria de conformidade fiscal"
            disabled={!canEditFields}
          />
        </label>

        <label className="audit-form__field">
          <span>Tipo de auditoria</span>
          <select
            value={value.tipoAuditoria}
            onChange={(event) => update("tipoAuditoria", event.target.value)}
            disabled={!canEditFields}
          >
            {tipoAuditoriaOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="audit-form__field">
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

        <label className="audit-form__field audit-form__field--span-2">
          <span>Escopo</span>
          <textarea
            value={value.escopo}
            onChange={(event) => update("escopo", event.target.value)}
            placeholder="Descreva unidades, processos e fronteiras da auditoria"
            disabled={!canEditFields}
            rows={4}
          />
        </label>

        <label className="audit-form__field">
          <span>Data início</span>
          <input
            type="date"
            value={value.dataInicio}
            onChange={(event) => update("dataInicio", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="audit-form__field">
          <span>Data fim</span>
          <input
            type="date"
            value={value.dataFim}
            onChange={(event) => update("dataFim", event.target.value)}
            disabled={!canEditFields}
          />
        </label>

        <label className="audit-form__field audit-form__field--span-2">
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
              placeholder="ID do responsavel"
              disabled={!canEditFields}
            />
          )}
          {!canReadUsers ? (
            <small className="audit-form__hint">
              Sem leitura de usuarios: informe o ID manualmente.
            </small>
          ) : null}
        </label>

        {!datesAreOrdered ? (
          <p className="audit-form__hint audit-form__hint--span-2">
            Data fim nao pode ser anterior a data início.
          </p>
        ) : null}
        {plannedMustNotHaveEndDate && value.dataFim.trim() !== "" ? (
          <p className="audit-form__hint audit-form__hint--span-2">
            Auditoria planejada nao deve ter data fim informada.
          </p>
        ) : null}
        {requiresStartDate && value.dataInicio.trim() === "" ? (
          <p className="audit-form__hint audit-form__hint--span-2">
            Status atual exige data inicio.
          </p>
        ) : null}
        {requiresEndDate && value.dataFim.trim() === "" ? (
          <p className="audit-form__hint audit-form__hint--span-2">
            Auditoria concluida exige data fim.
          </p>
        ) : null}
        {requiresResponsible && value.responsavelId.trim() === "" ? (
          <p className="audit-form__hint audit-form__hint--span-2">
            Auditoria em execução exige responsável.
          </p>
        ) : null}
      </div>

      <button
        type="button"
        className="audit-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar auditoria"}
      </button>
    </aside>
  );
}
