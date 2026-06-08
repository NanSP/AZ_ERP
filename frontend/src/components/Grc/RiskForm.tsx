import type { User } from "../../pages/Sys/UsersPage";
import type { RiskRecord } from "../../pages/Grc/RisksPage";

type RiskFormProps = {
  value: RiskRecord;
  editing: boolean;
  users: User[];
  canReadUsers: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: RiskRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const probabilityOptions = [
  { value: "1", label: "1 - Muito baixa" },
  { value: "2", label: "2 - Baixa" },
  { value: "3", label: "3 - Moderada" },
  { value: "4", label: "4 - Alta" },
  { value: "5", label: "5 - Muito alta" },
];

const impactOptions = [
  { value: "1", label: "1 - Muito baixo" },
  { value: "2", label: "2 - Baixo" },
  { value: "3", label: "3 - Moderado" },
  { value: "4", label: "4 - Alto" },
  { value: "5", label: "5 - Critico" },
];

export default function RiskForm({
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
}: RiskFormProps) {
  const riskLevel = value.nivelRisco || "";
  const riskIsHigh = riskLevel === "alto";
  const riskIsMedium = riskLevel === "medio";
  const needsResponsibleForPlan =
    value.planoMitigacao.trim() !== "" && (riskIsMedium || riskIsHigh);
  const canSave =
    value.titulo.trim() !== "" &&
    value.probabilidade.trim() !== "" &&
    value.impacto.trim() !== "" &&
    (!riskIsHigh ||
      (value.responsavelId.trim() !== "" && value.planoMitigacao.trim() !== "")) &&
    (!needsResponsibleForPlan || value.responsavelId.trim() !== "");

  function update<K extends keyof RiskRecord>(
    field: K,
    fieldValue: RiskRecord[K],
  ) {
    onChange({
      ...value,
      [field]: fieldValue,
    });
  }

  return (
    <aside className="risk-form">
      <div className="risk-form__head">
        <div>
          <h3 className="risk-form__title">
            {editing ? "Editar risco" : "Novo risco"}
          </h3>
          <p className="risk-form__subtitle">
            Classifique o risco, vincule um responsavel quando necessario e
            registre a estrategia de mitigacao.
          </p>
          {editing && value.id ? (
            <p className="risk-form__meta">Registro selecionado: #{value.id}</p>
          ) : !canEditFields ? (
            <p className="risk-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="risk-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="risk-form__grid">
        <label className="risk-form__field">
          <span>Codigo</span>
          <input
            value={value.codigo}
            onChange={(event) => update("codigo", event.target.value)}
            placeholder="RISK-001"
            disabled={!canEditFields}
          />
        </label>

        <label className="risk-form__field">
          <span>Categoria</span>
          <input
            value={value.categoria}
            onChange={(event) => update("categoria", event.target.value)}
            placeholder="Operacional, LGPD, Financeiro"
            disabled={!canEditFields}
          />
        </label>

        <label className="risk-form__field risk-form__field--span-2">
          <span>Titulo</span>
          <input
            value={value.titulo}
            onChange={(event) => update("titulo", event.target.value)}
            placeholder="Interrupcao de operacao critica"
            disabled={!canEditFields}
          />
        </label>

        <label className="risk-form__field risk-form__field--span-2">
          <span>Descricao</span>
          <textarea
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Descreva contexto, causa e impacto esperado"
            disabled={!canEditFields}
            rows={4}
          />
        </label>

        <label className="risk-form__field">
          <span>Probabilidade</span>
          <select
            value={value.probabilidade}
            onChange={(event) => update("probabilidade", event.target.value)}
            disabled={!canEditFields}
          >
            {probabilityOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="risk-form__field">
          <span>Impacto</span>
          <select
            value={value.impacto}
            onChange={(event) => update("impacto", event.target.value)}
            disabled={!canEditFields}
          >
            {impactOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="risk-form__field">
          <span>Nivel de risco</span>
          <input value={riskLevel || "-"} disabled />
        </label>

        <label className="risk-form__field">
          <span>Responsavel</span>
          {canReadUsers ? (
            <select
              value={value.responsavelId}
              onChange={(event) => update("responsavelId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Sem responsavel</option>
              {users.map((user) => (
                <option key={user.id ?? user.login} value={String(user.id ?? "")}>
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
            <small className="risk-form__hint">
              Sem leitura de usuarios: informe o ID manualmente.
            </small>
          ) : null}
        </label>

        <label className="risk-form__field risk-form__field--span-2">
          <span>Plano de mitigacao</span>
          <textarea
            value={value.planoMitigacao}
            onChange={(event) => update("planoMitigacao", event.target.value)}
            placeholder="Acoes, responsavel, prazo e contingencias"
            disabled={!canEditFields}
            rows={5}
          />
          {riskIsHigh ? (
            <small className="risk-form__hint">
              Risco alto exige responsavel e plano de mitigacao.
            </small>
          ) : needsResponsibleForPlan ? (
            <small className="risk-form__hint">
              Plano de mitigacao exige responsavel para risco medio.
            </small>
          ) : null}
        </label>
      </div>

      <button
        type="button"
        className="risk-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar risco"}
      </button>
    </aside>
  );
}
