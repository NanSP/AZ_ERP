import type { User } from "../../pages/Sys/UsersPage";
import type {
  ClientRecord,
} from "../../pages/Sd/ClientsPage";
import type { OpportunityRecord } from "../../pages/Sd/OpportunitiesPage";

type OpportunityFormProps = {
  value: OpportunityRecord;
  editing: boolean;
  clients: ClientRecord[];
  users: User[];
  canReadClients: boolean;
  canReadUsers: boolean;
  canEditFields: boolean;
  canSubmit: boolean;
  saving: boolean;
  onChange: (value: OpportunityRecord) => void;
  onSave: () => void;
  onReset: () => void;
};

const stageOptions = [
  { value: "prospeccao", label: "Prospeccao" },
  { value: "qualificacao", label: "Qualificacao" },
  { value: "proposta", label: "Proposta" },
  { value: "negociacao", label: "Negociacao" },
  { value: "fechado_ganho", label: "Fechado ganho" },
  { value: "fechado_perdido", label: "Fechado perdido" },
];

export default function OpportunityForm({
  value,
  editing,
  clients,
  users,
  canReadClients,
  canReadUsers,
  canEditFields,
  canSubmit,
  saving,
  onChange,
  onSave,
  onReset,
}: OpportunityFormProps) {
  const valorEstimado =
    value.valorEstimado.trim() === ""
      ? 0
      : Number(value.valorEstimado.replace(",", "."));
  const probabilidade =
    value.probabilidade.trim() === "" ? 50 : Number(value.probabilidade);
  const needsClosingDate =
    value.estagio === "proposta" ||
    value.estagio === "negociacao" ||
    value.estagio === "fechado_ganho" ||
    value.estagio === "fechado_perdido";
  const needsLossReason = value.estagio === "fechado_perdido";
  const canSave =
    value.clienteId.trim() !== "" &&
    value.responsavelId.trim() !== "" &&
    value.titulo.trim() !== "" &&
    value.estagio.trim() !== "" &&
    !Number.isNaN(valorEstimado) &&
    valorEstimado >= 0 &&
    !Number.isNaN(probabilidade) &&
    probabilidade >= 0 &&
    probabilidade <= 100 &&
    (!needsClosingDate || value.dataPrevistaFechamento.trim() !== "") &&
    (!needsLossReason || value.motivoPerda.trim() !== "") &&
    (needsLossReason || value.motivoPerda.trim() === "");

  function update<K extends keyof OpportunityRecord>(
    field: K,
    fieldValue: OpportunityRecord[K],
  ) {
    const nextValue = {
      ...value,
      [field]: fieldValue,
    };

    if (
      field === "estagio" &&
      fieldValue !== "fechado_perdido"
    ) {
      nextValue.motivoPerda = "";
    }

    if (
      field === "estagio" &&
      fieldValue !== "proposta" &&
      fieldValue !== "negociacao" &&
      fieldValue !== "fechado_ganho" &&
      fieldValue !== "fechado_perdido"
    ) {
      nextValue.dataPrevistaFechamento = "";
    }

    onChange(nextValue);
  }

  return (
    <aside className="opportunity-form">
      <div className="opportunity-form__head">
        <div>
          <h3 className="opportunity-form__title">
            {editing ? "Editar oportunidade" : "Nova oportunidade"}
          </h3>
          <p className="opportunity-form__subtitle">
            Avance negociações com cliente, responsável, valor estimado,
            probabilidade e estágio do funil comercial.
          </p>
          {editing && value.id ? (
            <p className="opportunity-form__meta">
              Registro selecionado: #{value.id}
            </p>
          ) : !canEditFields ? (
            <p className="opportunity-form__meta">
              Seu perfil possui acesso limitado para alteracoes neste recurso.
            </p>
          ) : null}
        </div>

        <button
          type="button"
          className="opportunity-form__ghost"
          onClick={onReset}
          disabled={saving}
        >
          Limpar
        </button>
      </div>

      <div className="opportunity-form__grid">
        <label className="opportunity-form__field opportunity-form__field--span-2">
          <span>Cliente</span>
          {canReadClients ? (
            <select
              value={value.clienteId}
              onChange={(event) => update("clienteId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um cliente</option>
              {clients.map((client) => (
                <option
                  key={client.id ?? client.parceiroId}
                  value={String(client.id ?? "")}
                >
                  Cliente #{client.id} - Parceiro {client.parceiroId}
                </option>
              ))}
            </select>
          ) : (
            <input
              value={value.clienteId}
              onChange={(event) =>
                update("clienteId", event.target.value.replace(/\D/g, ""))
              }
              placeholder="ID do cliente"
              disabled={!canEditFields}
            />
          )}
        </label>

        <label className="opportunity-form__field opportunity-form__field--span-2">
          <span>Titulo</span>
          <input
            value={value.titulo}
            onChange={(event) => update("titulo", event.target.value)}
            placeholder="Expansao de contrato, nova proposta, renovacao anual"
            disabled={!canEditFields}
          />
        </label>

        <label className="opportunity-form__field">
          <span>Responsavel</span>
          {canReadUsers ? (
            <select
              value={value.responsavelId}
              onChange={(event) => update("responsavelId", event.target.value)}
              disabled={!canEditFields}
            >
              <option value="">Selecione um responsavel</option>
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
        </label>

        <label className="opportunity-form__field">
          <span>Estagio</span>
          <select
            value={value.estagio}
            onChange={(event) => update("estagio", event.target.value)}
            disabled={!canEditFields}
          >
            {stageOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="opportunity-form__field">
          <span>Valor estimado</span>
          <input
            value={value.valorEstimado}
            onChange={(event) =>
              update(
                "valorEstimado",
                event.target.value.replace(/[^0-9,.-]/g, ""),
              )
            }
            placeholder="0"
            disabled={!canEditFields}
          />
        </label>

        <label className="opportunity-form__field">
          <span>Probabilidade (%)</span>
          <input
            value={value.probabilidade}
            onChange={(event) =>
              update("probabilidade", event.target.value.replace(/\D/g, ""))
            }
            placeholder="50"
            disabled={!canEditFields}
          />
        </label>

        <label className="opportunity-form__field">
          <span>Data prevista de fechamento</span>
          <input
            type="date"
            value={value.dataPrevistaFechamento}
            onChange={(event) =>
              update("dataPrevistaFechamento", event.target.value)
            }
            disabled={!canEditFields}
          />
        </label>

        <label className="opportunity-form__field">
          <span>Motivo da perda</span>
          <input
            value={value.motivoPerda}
            onChange={(event) => update("motivoPerda", event.target.value)}
            placeholder="Preco, timing, concorrencia, escopo"
            disabled={!canEditFields || !needsLossReason}
          />
        </label>

        <label className="opportunity-form__field opportunity-form__field--span-2">
          <span>Descricao</span>
          <textarea
            value={value.descricao}
            onChange={(event) => update("descricao", event.target.value)}
            placeholder="Contexto comercial, escopo, objeções, próximos passos e anotações relevantes"
            disabled={!canEditFields}
            rows={5}
          />
        </label>
      </div>

      <button
        type="button"
        className="opportunity-form__button"
        onClick={onSave}
        disabled={saving || !canSave || !canSubmit}
      >
        {saving
          ? "Salvando..."
          : editing
            ? "Salvar alteracoes"
            : "Criar oportunidade"}
      </button>
    </aside>
  );
}
